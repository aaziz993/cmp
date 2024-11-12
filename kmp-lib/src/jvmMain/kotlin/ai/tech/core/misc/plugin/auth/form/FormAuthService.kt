package ai.tech.core.misc.plugin.auth.form

import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.plugin.auth.AbstractChallengeAuthProvider
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.StorageAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.database.AuthRepository
import ai.tech.core.misc.plugin.auth.form.model.config.FormAuthConfig
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*

public class FormAuthService(
    override val name: String,
    override val config: FormAuthConfig,
    override val getRepository: (provider: String, database: String?, userTable: String?, roleTable: String?) -> AuthRepository?,
) : AuthProvider, StorageAuthProvider, ValidateAuthProvider<UserPasswordCredential>, AbstractChallengeAuthProvider {

    override suspend fun validate(call: ApplicationCall, credential: UserPasswordCredential): Any? =
        getRepository(name, config.database, config.principalTable, config.roleTable)?.getUserPassword(credential.name)?.first
            ?: userTable[credential.name]?.takeIf { credential.password }?.let{ User(credential.name)}

    override fun roles(principal: Any): Set<String> = (principal as User).roles ?: emptySet()
}
