package ai.tech.core.misc.config

import ai.tech.core.misc.model.config.ApplicationConfig
import ai.tech.core.misc.type.multiple.filterValuesIsNotEmpty
import ai.tech.core.misc.type.serializer.decodeFromAny
import kotlinx.serialization.json.Json

public class FileConfigService(
    name: String = "application",
    formats: List<String> = listOf("properties", "yaml", "yml", "json"),
    readFile: suspend (path: String) -> String?,
) : AbstractConfigService(name,formats, readFile) {

    override suspend fun readConfigs(): Map<String, Map<String, Any?>> {
        val config = readFileConfig()

        val applicationConfig: ApplicationConfig = Json.Default.decodeFromAny(config["application"])

        return mapOf(name to config) + applicationConfig.configurations.associateWith {
            readFileConfig("${applicationConfig.environment}/application-$it", formats)
        }.filterValuesIsNotEmpty()
    }
}
