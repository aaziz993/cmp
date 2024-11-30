package ai.tech.core.misc.plugin.auth.basic

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.misc.auth.model.identity.User
import ai.tech.core.misc.plugin.auth.AbstractStorageAuthProvider
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.DigesterAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.basic.model.config.BasicAuthConfig
import ai.tech.core.misc.auth.model.identity.principal.PrincipalEntity
import ai.tech.core.misc.auth.model.identity.role.RoleEntity
import io.ktor.server.application.*
import io.ktor.server.auth.*
import kotlinx.serialization.InternalSerializationApi

public class BasicAuthService(
    override val name: String?,
    override val config: BasicAuthConfig,
    getPrincipalRepository: (databaseName: String?, tableName: String) -> CRUDRepository<PrincipalEntity>? = { _, _ -> null },
    getRoleRepository: (databaseName: String?, tableName: String) -> CRUDRepository<RoleEntity>? = { _, _ -> null }
) : AuthProvider,
    DigesterAuthProvider,
    AbstractStorageAuthProvider(
        config,
        getPrincipalRepository,
        getRoleRepository,
    ),
    ValidateAuthProvider<UserPasswordCredential> {

    @OptIn(InternalSerializationApi::class)
    @Suppress("UNCHECKED_CAST")
    private val userHashedTableAuth: UserHashedTableAuth = UserHashedTableAuth(getDigester(), getUserTable())

    override suspend fun validate(call: ApplicationCall, credential: UserPasswordCredential): Any? =
        userHashedTableAuth.authenticate(credential)?.let(UserIdPrincipal::name)?.let(::User)
            ?: getUserPassword(credential.name)?.takeIf { (_, password) ->
                userHashedTableAuth.digester(credential.password) == password.toByteArray()
            }

    override fun roles(principal: Any): Set<String> = (principal as User).roles.orEmpty()
}
