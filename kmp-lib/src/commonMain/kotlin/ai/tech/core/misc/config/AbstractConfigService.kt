package ai.tech.core.misc.config

import ai.tech.core.misc.type.serializer.decoderMapFromString
import kotlinx.serialization.InternalSerializationApi
import ai.tech.core.misc.type.deepMerge
import org.lighthousegames.logging.KmLog
import org.lighthousegames.logging.logging

public abstract class AbstractConfigService(
    public val name: String = "application",
    public val formats: List<String> = listOf("properties", "yaml", "yml", "json"),
    private val readFile: suspend (String) -> String?,
) {

    protected val decoders: Map<String, (String) -> Map<String, Any?>> = formats.associateWith(::decoder)

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    protected suspend fun readFileConfig(path: String, formats: List<String>): Map<String, Any?> =
        formats.mapNotNull { format ->
            readFile("$path.$format")?.let(decoders[format]!!::invoke)
        }.deepMerge()

    protected suspend fun readFileConfig(): Map<String, Any?> = readFileConfig(name, formats)

    public abstract suspend fun readConfigs(): Map<String, Map<String, Any?>>

    @Suppress("UNCHECKED_CAST")
    private fun decoder(format: String): (String) -> Map<String, Any?> = {
        decoderMapFromString(format)(it) as Map<String, Any?>
    }

    public companion object {

        internal val log: KmLog = logging()
    }
}
