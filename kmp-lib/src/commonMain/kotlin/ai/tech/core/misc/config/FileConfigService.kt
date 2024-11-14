package ai.tech.core.misc.config

import ai.tech.core.misc.config.ConfigService.Companion.APPLICATION_CONFIG_NAME
import ai.tech.core.misc.model.config.ApplicationConfig
import ai.tech.core.misc.type.deepMerge
import ai.tech.core.misc.type.serializer.decodeFromAny
import ai.tech.core.misc.type.serializer.decoderMapFromString
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json

public class FileConfigService(
    public val formats: List<String> = listOf("properties", "yaml", "yml", "json"),
    public val readFile: suspend (path: String) -> String?,
) : ConfigService {

    private val decoders = formats.associateWith(::decoder)

    override suspend fun getConfig(): Map<String, Any?> {
        val fileConfig = readFileConfig(APPLICATION_CONFIG_NAME, formats)

        val application: ApplicationConfig = Json.Default.decodeFromAny(fileConfig["application"])

        return (listOf(fileConfig) + application.configurations.map {
            readFileConfig("${application.environment}/$APPLICATION_CONFIG_NAME-$it", formats)
        }).deepMerge()
    }

    @Suppress("UNCHECKED_CAST")
    private fun decoder(format: String): (String) -> Map<String, Any?> = {
        decoderMapFromString(format)(it) as Map<String, Any?>
    }


}
