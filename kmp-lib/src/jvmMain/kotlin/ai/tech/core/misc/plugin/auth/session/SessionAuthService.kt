package ai.tech.core.misc.plugin.auth.session

import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.AbstractChallengeAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.session.model.SessionAuthConfig
import ai.tech.core.misc.plugin.session.model.UserSession
import io.ktor.server.application.ApplicationCall

public class SessionAuthService(
    override val name: String,
    public val config: SessionAuthConfig,
) : AuthProvider, ValidateAuthProvider<UserSession>, AbstractChallengeAuthProvider, {

    override suspend fun validate(call: ApplicationCall, session: UserSession): Any? = session

    override fun roles(principal: Any): Set<String> = principal.roles
}
