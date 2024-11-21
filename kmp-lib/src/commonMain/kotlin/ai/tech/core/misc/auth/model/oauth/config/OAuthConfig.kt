package ai.tech.core.misc.auth.model.oauth.config

import ai.tech.core.misc.model.config.EnabledConfig

public interface OAuthConfig {

    public val provider: String
    public val address: String
    public val realm: String
    public val clientId: String
}
