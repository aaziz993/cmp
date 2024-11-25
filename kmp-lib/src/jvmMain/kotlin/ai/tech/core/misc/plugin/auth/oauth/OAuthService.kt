package ai.tech.core.misc.plugin.auth.oauth

import ai.tech.core.misc.auth.model.oauth.config.OAuthConfig
import ai.tech.core.misc.plugin.auth.AuthProvider
import io.ktor.server.application.ApplicationCall

public class OAuthService(
    override val name: String?,
    public val config: OAuthConfig,
) : AuthProvider
