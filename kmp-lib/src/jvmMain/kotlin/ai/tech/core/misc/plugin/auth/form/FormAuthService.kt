package ai.tech.core.misc.plugin.auth.form

import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.ChallengeAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.form.model.config.FormAuthConfig
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*

public class FormAuthService(
    override val name: String,
    public val config: FormAuthConfig,
) : AuthProvider, ValidateAuthProvider<UserPasswordCredential>, ChallengeAuthProvider {

    override suspend fun validate(call: ApplicationCall, credential: UserPasswordCredential): Any? =
        if (credential.name == "jetbrains" && credential.password == "foobar") {
            UserIdPrincipal(credential.name)
        }
        else {
            null
        }

    override fun roles(principal: Any): Set<String> = principal.roles ?: emptySet()
}
