package ai.tech.core.misc.config

import ai.tech.core.misc.model.config.ApplicationConfig
import ai.tech.core.misc.type.serializer.decodeFromAny
import kotlinx.serialization.json.Json

public class FileConfigService(
    formats: List<String> = listOf("properties", "yaml", "yml", "json"),
    readFile: suspend (path: String) -> String?,
) : AbstractConfigService(formats, readFile) {

    override suspend fun readConfigs(): Map<String, Map<String, Any?>> {
        val baseConfig = readBaseConfig()

        val applicationConfig: ApplicationConfig = Json.Default.decodeFromAny(baseConfig["application"])

        return mapOf(BASE_CONFIG_NAME to baseConfig) + applicationConfig.configurations.associateWith {
            readFileConfig("${applicationConfig.environment}/application-$it", formats)
        }.filterValues(Map<*, *>::isNotEmpty)
    }
}
