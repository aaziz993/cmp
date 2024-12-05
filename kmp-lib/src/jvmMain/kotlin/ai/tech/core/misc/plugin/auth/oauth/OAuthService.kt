package ai.tech.core.misc.plugin.auth.oauth

import ai.tech.core.misc.auth.client.oauth.config.OAuthConfig
import ai.tech.core.misc.plugin.auth.AuthProvider

public class OAuthService(
    override val name: String?,
    public val config: OAuthConfig,
) : AuthProvider
