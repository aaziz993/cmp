package ai.tech.core.misc.auth.client.oauth

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.auth.client.oauth.model.AuthenticationFailedCause
import ai.tech.core.misc.auth.client.oauth.model.OAuth1aException
import ai.tech.core.misc.auth.client.oauth.model.OAuth2Exception
import ai.tech.core.misc.auth.client.oauth.model.OAuth2RequestParameters
import ai.tech.core.misc.auth.client.oauth.model.OAuth2ResponseParameters
import ai.tech.core.misc.auth.client.oauth.model.OAuthAccessTokenResponse
import io.ktor.client.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.auth.*

public class OAuth2Explicit(
    name: String,
    httpClient: HttpClient,
    public val loginUrl: Url,
    callbackRedirectUrl: String,
    keyValue: AbstractKeyValue,
    onRedirectAuthenticate: suspend (url: Url) -> Unit
) : AbstractOAuth<OAuthAccessTokenResponse.OAuth2>(
    name,
    httpClient,
    callbackRedirectUrl,
    keyValue,
    onRedirectAuthenticate,
) {

    override suspend fun callback(parameters: Parameters): AuthenticationFailedCause? =
        try {
            resume(
                OAuthAccessTokenResponse.OAuth2(
                    accessToken = parameters[OAuth2ResponseParameters.AccessToken]
                        ?: throw OAuth2Exception.MissingAccessToken(),
                    tokenType = parameters[OAuth2ResponseParameters.TokenType] ?: "",
                    state = parameters[OAuth2RequestParameters.State],
                    expiresIn = parameters[OAuth2ResponseParameters.ExpiresIn]?.toLong() ?: 0L,
                    refreshToken = parameters[OAuth2ResponseParameters.RefreshToken],
                    extraParameters = parameters,
                ),
            )
            null
        }
        catch (e: OAuth2Exception) {
            AuthenticationFailedCause.Error(e.message.orEmpty())
        }

    override suspend fun getRedirectUrl(): Url =
        httpClient.get(loginUrl).headers[HttpHeaders.Location]!!.let(::Url)
}
