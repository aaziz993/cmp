package ai.tech.core.misc.plugin.auth.session

import ai.tech.core.misc.plugin.auth.AuthProvider
import ai.tech.core.misc.plugin.auth.ChallengeAuthProvider
import ai.tech.core.misc.plugin.auth.ValidateAuthProvider
import ai.tech.core.misc.plugin.auth.session.model.config.SessionAuthConfig
import ai.tech.core.misc.plugin.auth.session.model.UserSession
import io.ktor.server.application.ApplicationCall

public class SessionAuthService(
    override val name: String?,
    public val config: SessionAuthConfig,
) : AuthProvider, ValidateAuthProvider<UserSession>, ChallengeAuthProvider {

    override suspend fun validate(call: ApplicationCall, session: UserSession): Any? = session

    override fun roles(principal: Any): Set<String> = (principal as UserSession).roles
}
