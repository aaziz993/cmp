package ai.tech.core.misc.plugin.auth

import ai.tech.core.data.filesystem.pathExtension
import ai.tech.core.data.filesystem.readResourceBytes
import ai.tech.core.data.filesystem.readResourceProperties
import ai.tech.core.data.filesystem.readResourceText
import ai.tech.core.misc.plugin.auth.database.AuthRepository
import ai.tech.core.misc.plugin.auth.model.config.StorageAuthConfig
import ai.tech.core.misc.type.decodeAnyFromString
import java.io.ByteArrayInputStream
import java.util.Properties
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.orEmpty
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer
import net.mamoe.yamlkt.Yaml

public interface StorageAuthProvider {

    public val config: StorageAuthConfig

    public val getRepository: (provider: String, database: String?, userTable: String?, roleTable: String?) -> AuthRepository?

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    public val userTable: Map<String, ByteArray>
        get() = config.file?.fold(emptyMap<String, ByteArray>()) { acc, file ->
            acc + when (file.pathExtension) {
                "json" -> Json.Default.decodeAnyFromString(JsonObject::class.serializer(), readResourceText(file)!!) as Map<String, String>

                "properties" -> readResourceProperties(file).entries.associate { (k, v) -> k.toString() to v.toString() }

                else -> throw IllegalArgumentException("Unsupported file format: $file")
            }.mapValues { (_, v) -> v.toByteArray() }
        }.orEmpty()
}
