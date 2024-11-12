package ai.tech.core.misc.plugin.auth

import ai.tech.core.data.filesystem.pathExtension
import ai.tech.core.data.filesystem.readResourceProperties
import ai.tech.core.data.filesystem.readResourceText
import ai.tech.core.misc.plugin.auth.database.AuthRepository
import ai.tech.core.misc.plugin.auth.model.config.StoreAuthProviderConfig
import ai.tech.core.misc.type.decodeAnyFromString
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.orEmpty
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.serializer

public interface StoreAuthProvider {

    public val name: String

    public val config: StoreAuthProviderConfig

    public val getRepository: (provider: String, database: String?, principalTable: String?, roleTable: String?) -> AuthRepository?

    public fun getRepository(): AuthRepository? = with(config) {
        getRepository(name, database, principalTable, roleTable)
    }

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    public fun getUserTable(): Map<String, ByteArray> =
        config.file?.fold(emptyMap<String, ByteArray>()) { acc, file ->
            acc + when (file.pathExtension) {
                "json" -> Json.Default.decodeAnyFromString(JsonObject::class.serializer(), readResourceText(file)!!) as Map<String, String>

                "properties" -> readResourceProperties(file).entries.associate { (k, v) -> k.toString() to v.toString() }

                else -> throw IllegalArgumentException("Unsupported file extension: ${file.pathExtension}")
            }.mapValues { (_, v) -> v.toByteArray() }
        }.orEmpty()
}
