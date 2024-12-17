package ai.tech.core.misc.auth.client.oauth

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.auth.client.oauth.model.AuthenticationFailedCause
import ai.tech.core.misc.auth.client.oauth.model.OAuth1aException
import ai.tech.core.misc.auth.client.oauth.model.OAuthAccessTokenResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.auth.*

public class OAuth1aExplicitProvider(
    name: String?,
    httpClient: HttpClient,
    public val loginUrl: Url,
    callbackRedirectUrl: String,
    keyValue: AbstractKeyValue,
    onRedirectAuthenticate: suspend (url: Url) -> Unit
) : AbstractOAuthProvider<OAuthAccessTokenResponse.OAuth1a>(
    name,
    httpClient,
    callbackRedirectUrl,
    keyValue,
    onRedirectAuthenticate,
) {

    override suspend fun callback(parameters: Parameters): AuthenticationFailedCause? =
        try {
            resume(
                OAuthAccessTokenResponse.OAuth1a(
                    parameters[HttpAuthHeader.Parameters.OAuthToken]
                        ?: throw OAuth1aException.MissingTokenException(),
                    parameters[HttpAuthHeader.Parameters.OAuthTokenSecret]
                        ?: throw OAuth1aException.MissingTokenException(),
                    parameters,
                ),
            )
            null
        }
        catch (e: OAuth1aException) {
            AuthenticationFailedCause.Error(e.message.orEmpty())
        }

    override suspend fun getRedirectUrl(): Url =
        httpClient.get(loginUrl).headers[HttpHeaders.Location]!!.let(::Url)
}
