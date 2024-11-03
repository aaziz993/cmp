package ai.tech.core.misc.auth.model.oauth.config

import ai.tech.core.misc.model.config.EnabledConfig

public interface OAuthConfig : EnabledConfig {
    public val address: String
    public val realm: String
    public val clientId: String
    public val clientSecret: String?
}
