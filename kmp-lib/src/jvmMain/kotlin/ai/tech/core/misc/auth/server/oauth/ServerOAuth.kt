package ai.tech.core.misc.auth.server.oauth

import ai.tech.core.misc.auth.model.oauth.config.OAuthConfig
import ai.tech.core.misc.auth.server.ServerAuthProvider

public class ServerOAuth(
    override val name: String,
    public val config: OAuthConfig,
) : ServerAuthProvider<Any>
