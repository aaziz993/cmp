package ai.tech.core.misc.plugin.auth.form

import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.plugin.auth.ChallengeAuthProvider
import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.DigesterAuthProvider
import ai.tech.core.misc.plugin.auth.StoreAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.database.AuthRepository
import ai.tech.core.misc.plugin.auth.form.model.config.FormAuthConfig
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*

public class FormAuthService(
    override val name: String,
    override val config: FormAuthConfig,
    override val getRepository: (provider: String, database: String?, userTable: String?, roleTable: String?) -> AuthRepository?,
) : AuthProvider,
    DigesterAuthProvider,
    StoreAuthProvider,
    ValidateAuthProvider<UserPasswordCredential>,
    ChallengeAuthProvider {

    private val digester = getDigester()

    private val userTable = getUserTable()

    private val repository = getRepository()

    override suspend fun validate(call: ApplicationCall, credential: UserPasswordCredential): Any? =
        if (userTable[credential.name] == digester(credential.password)) {
            User(credential.name)
        }
        else {
            repository?.getUserPassword(credential.name)?.first
        }

    override fun roles(principal: Any): Set<String> = (principal as User).roles ?: emptySet()
}
