package ai.tech.core.misc.auth.client.oauth.config

public interface OAuthConfig {

    public val provider: String
    public val address: String
    public val realm: String
    public val clientId: String
}
