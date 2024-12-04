package ai.tech.core.misc.config

import ai.tech.core.misc.consul.client.kv.KVClient
import ai.tech.core.misc.consul.client.kv.model.Value
import ai.tech.core.misc.consul.model.config.Config
import ai.tech.core.misc.consul.model.config.ConsulConfig
import ai.tech.core.misc.model.config.ApplicationConfig
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.network.http.client.createHttpClient
import ai.tech.core.misc.type.serialization.serializer.decoderMapFromString
import kotlinx.serialization.InternalSerializationApi
import ai.tech.core.misc.type.deepMerge
import ai.tech.core.misc.type.multiple.filterValuesNotEmpty
import ai.tech.core.misc.type.serialization.decodeFromAny
import ai.tech.core.misc.util.run
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.lighthousegames.logging.KmLog
import org.lighthousegames.logging.logging

public class ConfigService<T : Any>(
    private val serializer: KSerializer<T>,
    public val name: String = "application",
    public val formats: List<String> = listOf("properties", "yaml", "yml", "json"),
    private val readFile: suspend (String) -> String?,
) {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val decoders: Map<String, (String) -> Map<String, Any?>> = formats.associateWith(::decoder)

    public suspend fun readConfig(): T {
        val config = readFileConfig()

        val application: ApplicationConfig = Json.Default.decodeFromAny(config["application"])

        val fileConfigs = readFileConfigs(application) + config

        val consul: ConsulConfig? = config["consul"]?.let { Json.Default.decodeFromAny(it) }

        val consulConfigs = consul?.takeIf(EnabledConfig::enabled)?.config?.takeIf(EnabledConfig::enabled)?.let {
            readConsulConfigs(
                createHttpClient {
                    install(ContentNegotiation) {
                        json(
                            Json {
                                isLenient = true
                                ignoreUnknownKeys = true
                                explicitNulls = false
                            },
                        )
                    }
                },
                consul.address,
                application,
                it,
            )

        } ?: emptyMap()

        return Json.Default.decodeFromAny(serializer, listOf(consulConfigs + fileConfigs).deepMerge())
    }

    private suspend fun readFileConfig(): Map<String, Any?> = readFileConfig(name, formats)

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private suspend fun readFileConfig(path: String, formats: List<String>): Map<String, Any?> =
        formats.mapNotNull { format ->
            readFile("$path.$format")?.let(decoders[format]!!::invoke)
        }.deepMerge()

    private suspend fun readFileConfigs(config: ApplicationConfig): Map<String, Any?> =
        config.configurations.map {
            readFileConfig("${config.environment}/application-$it", formats)
        }.deepMerge()

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private suspend fun KVClient.readConsulConfig(path: String, format: String): Map<String, Any?> =
        getValue(path).mapNotNull(Value::decodedValue)
            .map { decoders[format]!!(it) }.deepMerge()

    private suspend fun readConsulConfigs(
        httpClient: HttpClient,
        address: String,
        applicationConfig: ApplicationConfig,
        config: Config,
    ): Map<String, Any?> = with(config) {
        return try {
            return run(
                config.retry,
                { exception, attempt ->
                    log.w(exception) { "Couldn't load consul configuration from \"$address\" in attempt \"$attempt\"" }
                },
            ) {
                val kvClient = KVClient(httpClient, address, aclToken)

                with(applicationConfig) {
                    getKeys(
                        name,
                        configurations.map { "${environment}/$it" },
                    ).map { kvClient.readConsulConfig(it, format) }.deepMerge()
                }
            }
        }
        catch (e: HttpRequestTimeoutException) {
            if (failFast) {
                throw e
            }

            log.w(e) { "Couldn't load consul configuration from \"$address\"" }

            emptyMap()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun decoder(format: String): (String) -> Map<String, Any?> = {
        decoderMapFromString(format)(it) as Map<String, Any?>
    }

    public companion object {

        private val log: KmLog = logging("ConfigService")
    }
}
