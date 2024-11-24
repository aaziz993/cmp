package ai.tech.core.misc.plugin.auth.form

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.plugin.auth.AbstractStorageAuthProvider
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.ChallengeAuthProvider
import ai.tech.core.misc.plugin.auth.DigesterAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.database.kotysa.principal.model.PrincipalEntity
import ai.tech.core.misc.plugin.auth.database.kotysa.role.model.RoleEntity
import ai.tech.core.misc.plugin.auth.form.model.config.FormAuthConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*

public class FormAuthService(
    override val name: String,
    override val config: FormAuthConfig,
    getPrincipalRepository: (databaseName: String?, tableName: String) -> CRUDRepository<PrincipalEntity>? = { _, _ -> null },
    getRoleRepository: (databaseName: String?, tableName: String) -> CRUDRepository<RoleEntity>? = { _, _ -> null },
) : AuthProvider,
    DigesterAuthProvider,
    AbstractStorageAuthProvider(
        config,
        getPrincipalRepository,
        getRoleRepository,
    ),
    ValidateAuthProvider<UserPasswordCredential>,
    ChallengeAuthProvider {

    private val digester = getDigester()

    private val userTable = getUserTable()

    override suspend fun validate(call: ApplicationCall, credential: UserPasswordCredential): Any? =
        if (userTable[credential.name] == digester(credential.password)) {
            User(credential.name)
        }
        else {
            getUserPassword(credential.name)?.first
        }

    override fun roles(principal: Any): Set<String> = (principal as User).roles ?: emptySet()
}
