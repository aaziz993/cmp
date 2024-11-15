package ai.tech.core.misc.config

import ai.tech.core.misc.consul.client.ConsulClient
import ai.tech.core.misc.consul.client.kv.model.Value
import ai.tech.core.misc.consul.model.config.ConsulConfig
import ai.tech.core.misc.model.config.ApplicationConfig
import ai.tech.core.misc.type.serializer.decodeFromAny
import io.ktor.client.HttpClient
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json

public class ConsulConfigService(
    public val httpClient: HttpClient,
    formats: List<String> = listOf("properties", "yaml", "yml", "json"),
    readFile: suspend (path: String) -> String?,
) : AbstractConfigService(formats, readFile) {

    override suspend fun readConfigs(): Map<String, Map<String, Any?>> {
        val baseConfig = readBaseConfig()

        val applicationConfig: ApplicationConfig = Json.Default.decodeFromAny(baseConfig["application"])

        val consulConfig: ConsulConfig? = baseConfig["consul"]?.let(Json.Default::decodeFromAny)

        if (consulConfig?.config == null) {
            return emptyMap()
        }

        val consulClient = ConsulClient(httpClient, consulConfig.address)

        return with(consulConfig.config) {
            generateKeys(applicationConfig.configurations).associateWith {
                consulClient.readConsulConfig("$it/${applicationConfig.environment}", format)
            }.filterValues(Map<*, *>::isEmpty)
        }
    }

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private suspend fun ConsulClient.readConsulConfig(path: String, format: String) =
        kv.getValue(path).mapNotNull(Value::decodedValue).fold(emptyMap<String, Any?>()) { acc, v -> acc + decoders[format]!!(v) }
}
