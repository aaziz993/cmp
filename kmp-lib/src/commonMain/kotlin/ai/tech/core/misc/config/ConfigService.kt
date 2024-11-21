package ai.tech.core.misc.config

import ai.tech.core.misc.consul.client.kv.KVClient
import ai.tech.core.misc.consul.client.kv.model.Value
import ai.tech.core.misc.consul.model.config.ConsulConfig
import ai.tech.core.misc.model.config.ApplicationConfig
import ai.tech.core.misc.type.serializer.decoderMapFromString
import kotlinx.serialization.InternalSerializationApi
import ai.tech.core.misc.type.deepMerge
import ai.tech.core.misc.type.multiple.filterValuesNotEmpty
import ai.tech.core.misc.type.serializer.decodeFromAny
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import kotlinx.serialization.json.Json
import org.lighthousegames.logging.KmLog
import org.lighthousegames.logging.logging

public class ConfigService(
    public val name: String = "application",
    public val formats: List<String> = listOf("properties", "yaml", "yml", "json"),
    private val readFile: suspend (String) -> String?,
    private val httpClient: HttpClient? = null,
) {

    private val decoders: Map<String, (String) -> Map<String, Any?>> = formats.associateWith(::decoder)

    public suspend fun readConfig() {
        val config = readFileConfig()

        val application: ApplicationConfig = Json.Default.decodeFromAny(config["application"])

        val applicationConfigs = readFileConfigs(application)

        val consul: ConsulConfig? = config["consul"]?.let { Json.Default.decodeFromAny(it) }

        if (!(httpClient == null || consul == null)) {

            val consulConfigs = readConsulConfigs(httpClient, application, consul)
        }
    }

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private suspend fun readFileConfig(path: String, formats: List<String>): Map<String, Any?> =
        formats.mapNotNull { format ->
            readFile("$path.$format")?.let(decoders[format]!!::invoke)
        }.deepMerge()

    private suspend fun readFileConfig(): Map<String, Any?> = readFileConfig(name, formats)

    private suspend fun readFileConfigs(config: ApplicationConfig): Map<String, Any?> =
        config.configurations.associateWith {
            readFileConfig("${config.environment}/application-$it", formats)
        }.filterValuesNotEmpty()

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private suspend fun KVClient.readConsulConfig(path: String, format: String) =
        getValue(path).mapNotNull(Value::decodedValue)
            .fold(emptyMap<String, Any?>()) { acc, v -> acc + decoders[format]!!(v) }

    private suspend fun readConsulConfigs(
        httpClient: HttpClient,
        config: ApplicationConfig,
        consulConfig: ConsulConfig,
    ): Map<String, Map<String, Any?>> {
        if (consulConfig.config == null) {
            return emptyMap()
        }
        return with(consulConfig.config) {
            return try {
                val kvClient = KVClient(httpClient, consulConfig.address, aclToken)

                getKeys(
                    config.name,
                    config.configurations.map { "$it/${config.environment}" }).associateWith {
                    kvClient.readConsulConfig(it, format)
                }.filterValuesNotEmpty()
            } catch (e: HttpRequestTimeoutException) {
                if (failFast) {
                    throw e
                }

                log.w(e) { "Couldn't load consul configuration from: ${consulConfig.address}" }

                emptyMap()
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun decoder(format: String): (String) -> Map<String, Any?> = {
        decoderMapFromString(format)(it) as Map<String, Any?>
    }

    public companion object {

        internal val log: KmLog = logging()
    }
}
