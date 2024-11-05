package ai.tech.core.misc.auth.model.oauth.config

public interface OAuthConfig {

    public val provider: String
    public val address: String
    public val realm: String
    public val clientId: String
    public val clientSecret: String?
}
