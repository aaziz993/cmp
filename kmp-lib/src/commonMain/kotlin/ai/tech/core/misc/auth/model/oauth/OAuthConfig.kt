package ai.tech.core.misc.auth.model.oauth

import ai.tech.core.misc.config.EnablableConfig

public interface OAuthConfig : EnablableConfig {
    public val address: String
    public val realm: String
    public val clientId: String
    public val clientSecret: String?
}
