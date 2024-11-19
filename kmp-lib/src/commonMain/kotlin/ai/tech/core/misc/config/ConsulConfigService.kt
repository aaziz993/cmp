package ai.tech.core.misc.config

import ai.tech.core.misc.consul.client.ConsulClient
import ai.tech.core.misc.consul.client.kv.model.Value
import ai.tech.core.misc.consul.model.config.ConsulConfig
import ai.tech.core.misc.model.config.ApplicationConfig
import ai.tech.core.misc.type.multiple.filterValuesIsNotEmpty
import ai.tech.core.misc.type.serializer.decodeFromAny
import io.ktor.client.HttpClient
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json

public class ConsulConfigService(
    public val httpClient: HttpClient,
    name: String = "application",
    formats: List<String> = listOf("properties", "yaml", "yml", "json"),
    readFile: suspend (path: String) -> String?,
) : AbstractConfigService(name, formats, readFile) {

    override suspend fun readConfigs(): Map<String, Map<String, Any?>> {
        val config = readFileConfig()

        val applicationConfig: ApplicationConfig = Json.Default.decodeFromAny(config["application"])

        val consulConfig: ConsulConfig? = config["consul"]?.let(Json.Default::decodeFromAny)

        if (consulConfig?.config == null) {
            return emptyMap()
        }

        return with(consulConfig.config) {
            val consulClient = ConsulClient(httpClient, consulConfig.address, aclToken)

            getKeys(applicationConfig.name, applicationConfig.configurations.map { "$it/${applicationConfig.environment}" }).associateWith {
                consulClient.readConsulConfig(it, format)
            }.filterValuesIsNotEmpty()
        }
    }

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private suspend fun ConsulClient.readConsulConfig(path: String, format: String) =
        kv.getValue(path).mapNotNull(Value::decodedValue).fold(emptyMap<String, Any?>()) { acc, v -> acc + decoders[format]!!(v) }
}
