package ai.tech.core.misc.plugin.auth

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.expression.f
import ai.tech.core.data.filesystem.pathExtension
import ai.tech.core.data.filesystem.readResourceProperties
import ai.tech.core.data.filesystem.readResourceText
import ai.tech.core.misc.auth.model.identity.User
import ai.tech.core.misc.auth.model.identity.principal.PrincipalEntity
import ai.tech.core.misc.auth.model.identity.role.RoleEntity
import ai.tech.core.misc.plugin.auth.model.config.StoreAuthProviderConfig
import ai.tech.core.misc.type.serialization.serializer.decodeMapFromString
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.ifEmpty
import kotlin.collections.orEmpty
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toSet
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json

public abstract class AbstractStorageAuthProvider(
    private val config: StoreAuthProviderConfig,
    getPrincipalRepository: (databaseName: String?, tableName: String) -> CRUDRepository<PrincipalEntity>? = { _, _ -> null },
    getRoleRepository: (databaseName: String?, tableName: String) -> CRUDRepository<RoleEntity>? = { _, _ -> null }
) {

    private val principalRepository = config.principalTable?.let { getPrincipalRepository(config.database, it) }

    private val roleRepository = config.roleTable?.let { getRoleRepository(config.database, it) }

    public suspend fun getPrincipal(username: String): PrincipalEntity? = principalRepository?.transactional {
        find(predicate = "username".f.eq(username)).singleOrNull()
    }

    public suspend fun getUserPassword(username: String): Pair<User, String>? = principalRepository?.transactional {
        getPrincipal(username)?.let { principal ->

            User(
                principal.username,
                roles = roleRepository?.let {
                    it.find(predicate = "userId".f.eq(principal.id)).map { it.name }.toSet().ifEmpty { null }
                },
            ) to principal.password
        }
    }

    @Suppress("UNCHECKED_CAST")
    @OptIn(InternalSerializationApi::class)
    public fun getUserTable(): Map<String, ByteArray> =
        config.file?.fold(emptyMap<String, ByteArray>()) { acc, file ->
            acc + when (file.pathExtension) {
                "json" -> Json.Default.decodeMapFromString(readResourceText(file)!!) as Map<String, String>

                "properties" -> readResourceProperties(file).entries.associate { (k, v) -> k.toString() to v.toString() }

                else -> throw IllegalArgumentException("Unsupported file extension: ${file.pathExtension}")
            }.mapValues { (_, v) -> v.toByteArray() }
        }.orEmpty()
}
