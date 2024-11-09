package ai.tech.core.misc.plugin.auth.bearer

import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.bearer.model.config.BearerAuthConfig
import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.*

public class BearerAuthService(
    override val name: String,
    public val config: BearerAuthConfig,
) : AuthProvider, ValidateAuthProvider<BearerTokenCredential> {

    override suspend fun validate(call: ApplicationCall, credential: BearerTokenCredential): Any? =
        if (credential.token == "abc123") {
            UserIdPrincipal("jetbrains")
        }
        else {
            null
        }

    override fun roles(principal: Any): Set<String> = principal.roles.orEmpty()
}
