package ai.tech.core.misc.auth.client.oauth.model

import ai.tech.core.misc.auth.client.bearer.model.BearerToken
import io.ktor.http.*

/**
 * An OAuth access token acquired from the server.
 */
public sealed class OAuthAccessTokenResponse : BearerToken {

    /**
     * OAuth1a access token acquired from the server
     * @property token itself
     * @property tokenSecret token secret to be used with [token]
     * @property extraParameters contains additional parameters provided by the server
     */
    public data class OAuth1a(
        override val token: String,
        val tokenSecret: String,
        val extraParameters: Parameters = Parameters.Empty
    ) : OAuthAccessTokenResponse()

    /**
     * OAuth2 access token acquired from the server
     * @property token access token from server
     * @property tokenType OAuth2 token type (usually Bearer)
     * @property expiresIn token expiration timestamp
     * @property refreshToken to be used to refresh access token after expiration
     * @property state generated state used for the OAuth procedure
     * @property extraParameters contains additional parameters provided by the server
     */
    public data class OAuth2(
        override val token: String,
        val tokenType: String,
        val expiresIn: Long,
        override val refreshToken: String?,
        val extraParameters: Parameters = Parameters.Empty,
    ) : OAuthAccessTokenResponse() {

        public var state: String? = null
            private set

        public constructor(
            accessToken: String,
            tokenType: String,
            expiresIn: Long,
            refreshToken: String?,
            extraParameters: Parameters = Parameters.Empty,
            state: String? = null
        ) : this(accessToken, tokenType, expiresIn, refreshToken, extraParameters) {
            this.state = state
        }
    }
}

