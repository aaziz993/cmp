package ai.tech.core.misc.auth.client.oauth.model

/**
 * Represents a cause for an authentication challenge request.
 */
public sealed class AuthenticationFailedCause {

    /**
     * Represents a case when no credentials are provided.
     */
    public data object NoCredentials : AuthenticationFailedCause()

    /**
     * Represents a case when invalid credentials are provided.
     */
    public data object InvalidCredentials : AuthenticationFailedCause()

    /**
     * Represents a case when authentication mechanism failed.
     * @param message describing the cause of the authentication failure.
     */
    public open class Error(public val message: String) : AuthenticationFailedCause()
}

public class OAuth2RedirectError(public val error: String, public val errorDescription: String?) :
    AuthenticationFailedCause.Error(if (errorDescription == null) error else "$error: $errorDescription")

