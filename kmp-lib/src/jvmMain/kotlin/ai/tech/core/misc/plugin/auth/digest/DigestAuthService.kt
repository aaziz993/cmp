package ai.tech.core.misc.plugin.auth.digest

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.misc.auth.model.identity.User
import ai.tech.core.misc.plugin.auth.AbstractStorageAuthProvider
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.DigestAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.auth.model.identity.principal.PrincipalEntity
import ai.tech.core.misc.auth.model.identity.role.RoleEntity
import ai.tech.core.misc.plugin.auth.digest.model.config.DigestAuthConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*

public class DigestAuthService(
    override val name: String?,
    public val config: DigestAuthConfig,
    getPrincipalRepository: (databaseName: String?, tableName: String) -> CRUDRepository<PrincipalEntity>? = { _, _ -> null },
    getRoleRepository: (databaseName: String?, tableName: String) -> CRUDRepository<RoleEntity>? = { _, _ -> null }
) : AuthProvider, DigestAuthProvider, AbstractStorageAuthProvider(
    config,
    getPrincipalRepository,
    getRoleRepository,
), ValidateAuthProvider<DigestCredential> {

    private val userTable = getUserTable()

    override suspend fun digestProvider(userName: String, realm: String): ByteArray? =
        userTable[userName] ?: getPrincipal(userName)?.password?.let(String::toByteArray)

    override suspend fun validate(call: ApplicationCall, credential: DigestCredential): Any? =
        if (userTable.contains(credential.userName)) {
            User(credential.userName)
        }
        else {
            getUserPassword(credential.userName)?.first
        }

    override fun roles(principal: Any): Set<String> = (principal as User).roles.orEmpty()
}
