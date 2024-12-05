package ai.tech.core.misc.auth.client.oauth.model

/**
 * An OAuth1a server error.
 */
public sealed class OAuth1aException(message: String) : Exception(message) {

    /**
     * Thrown when an OAuth1a server didn't provide access token.
     */
    public class MissingTokenException : OAuth1aException("The OAuth1a server didn't provide access token")
}
