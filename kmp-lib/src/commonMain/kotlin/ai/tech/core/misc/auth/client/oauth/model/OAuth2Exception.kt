package ai.tech.core.misc.auth.client.oauth.model

import io.ktor.util.internal.initCauseBridge
import kotlinx.coroutines.CopyableThrowable
import kotlinx.coroutines.ExperimentalCoroutinesApi

/**
 * Represents an error during communicating to OAuth2 server.
 * @property errorCode OAuth2 server replied with
 */
public sealed class OAuth2Exception(message: String, public val errorCode: String?) : Exception(message) {
    /**
     * Thrown when OAuth2 server responds with the "invalid_grant" error.
     */
    public class InvalidGrant(message: String) : OAuth2Exception(message, "invalid_grant")

    /**
     * Thrown when a nonce verification failed.
     */
    public class InvalidNonce : OAuth2Exception("Nonce verification failed", null)

    /**
     * Thrown when an OAuth2 server responds with a successful HTTP status and expected content type that was successfully
     * decoded but the response doesn't contain a error code nor access token.
     */
    public class MissingAccessToken : OAuth2Exception(
        "OAuth2 server response is OK neither error nor access token provided",
        null
    )

    /**
     * Thrown when an OAuth2 server responds with the "unsupported_grant_type" error.
     * @param grantType that was passed to the server
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    public class UnsupportedGrantType(public val grantType: String) :
        OAuth2Exception(
            "OAuth2 server doesn't support grant type $grantType",
            "unsupported_grant_type"
        ),
        CopyableThrowable<UnsupportedGrantType> {
        override fun createCopy(): UnsupportedGrantType = UnsupportedGrantType(grantType).also {
            it.initCauseBridge(this)
        }
    }

    /**
     * Thrown when an OAuth2 server responds with [errorCode].
     * @param errorCode the OAuth2 server replied with
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    public class UnknownException(
        private val details: String,
        errorCode: String
    ) : OAuth2Exception("$details (error code = $errorCode)", errorCode), CopyableThrowable<UnknownException> {
        override fun createCopy(): UnknownException = UnknownException(details, errorCode!!).also {
            it.initCauseBridge(this)
        }
    }
}
