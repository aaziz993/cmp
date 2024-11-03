package ai.tech.core.misc.auth.server.session

import ai.tech.core.misc.auth.server.ServerAuthProvider
import ai.tech.core.misc.auth.server.ServerChallengeAuth
import ai.tech.core.misc.plugin.session.model.UserSession
import ai.tech.core.misc.auth.server.session.model.ServerSessionAuthConfig

public class ServerSessionAuth(
    override val name: String,
    public val config: ServerSessionAuthConfig,
) : ServerChallengeAuth,
    ServerAuthProvider<UserSession> {

    public fun validate(session: UserSession): Any? = session

    override fun roles(principal: UserSession): Set<String> = principal.roles
}
