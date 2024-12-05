package ai.tech.core.misc.auth.client.oauth.model

/**
 * List of OAuth2 server response parameters.
 */

public object OAuth2ResponseParameters {
    public const val AccessToken: String = "access_token"
    public const val TokenType: String = "token_type"
    public const val ExpiresIn: String = "expires_in"
    public const val RefreshToken: String = "refresh_token"
    public const val Error: String = "error"
    public const val ErrorDescription: String = "error_description"
}
