package ai.tech.core.misc.auth.client.oauth.model

/**
 * List of OAuth2 request parameters for both peers.
 */

public object OAuth2RequestParameters {
    public const val ClientId: String = "client_id"
    public const val Scope: String = "scope"
    public const val ClientSecret: String = "client_secret"
    public const val GrantType: String = "grant_type"
    public const val Code: String = "code"
    public const val Error: String = "error"
    public const val ErrorDescription: String = "error_description"
    public const val State: String = "state"
    public const val RedirectUri: String = "redirect_uri"
    public const val ResponseType: String = "response_type"
    public const val UserName: String = "username"
    public const val Password: String = "password"
}
