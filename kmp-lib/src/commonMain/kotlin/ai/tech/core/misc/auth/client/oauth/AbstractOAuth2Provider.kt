package ai.tech.core.misc.auth.client.oauth

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.auth.client.bearer.model.BearerTokenImpl
import ai.tech.core.misc.auth.client.oauth.model.OAuthAccessTokenResponse
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.providers.RefreshTokensParams
import io.ktor.client.request.forms.submitForm
import io.ktor.http.Url
import io.ktor.http.parameters

public abstract class AbstractOAuth2Provider(
    name: String?,
    httpClient: HttpClient,
    public val refreshTokenUrl: String,
    public val clientId: String,
    callbackRedirectUrl: String,
    keyValue: AbstractKeyValue,
    onRedirectAuthenticate: suspend (url: Url) -> Unit
) : AbstractOAuthProvider<OAuthAccessTokenResponse.OAuth2>(
    name,
    httpClient,
    callbackRedirectUrl,
    keyValue,
    onRedirectAuthenticate
) {

    override suspend fun RefreshTokensParams.refreshToken(): BearerTokenImpl? =
        client.submitForm(
            url = refreshTokenUrl,
            formParameters = parameters {
                append("grant_type", "refresh_token")
                append("client_id", clientId)
                append("refresh_token", oldTokens?.refreshToken.orEmpty())
            },
        ) { markAsRefreshTokenRequest() }.body()
}
