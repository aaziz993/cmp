package ai.tech.core.misc.auth.client.bearer

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.data.keyvalue.get
import ai.tech.core.misc.auth.client.AbstractAuthService
import ai.tech.core.misc.auth.client.bearer.model.BearerToken
import ai.tech.core.misc.auth.client.bearer.model.BearerTokenImpl
import ai.tech.core.misc.network.http.client.httpUrl
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.AuthConfig
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.RefreshTokensParams
import io.ktor.client.plugins.auth.providers.bearer
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer

@OptIn(InternalSerializationApi::class)
public abstract class AbstractBearerAuthService(
    name: String?,
    httpClient: HttpClient,
    public val keyValue: AbstractKeyValue
) : AbstractAuthService<BearerToken>(
    name,
    httpClient,
    BearerTokenImpl::class.serializer(),
    keyValue,
) {

    override fun AuthConfig.configureAuth() {
        bearer {
            loadTokens { getToken()?.let { BearerTokens(it.token, it.refreshToken) } }

            refreshTokens {
                try {
                    refreshToken()?.let { token ->

                        setToken(token)

                        BearerTokens(token.token, oldTokens?.refreshToken!!)
                    }
                }
                catch (_: Throwable) {
                    null
                }.also {
                    // Handle invalid refresh token
                    if (it == null) {
                        signOut()
                    }
                }
            }

            sendWithoutRequest { request ->
                request.url.host == address.httpUrl.host
            }
        }
    }

    protected abstract suspend fun RefreshTokensParams.refreshToken(): BearerToken?

    final override suspend fun signOut() {
        httpClient.authProvider<BearerAuthProvider>()?.clearToken()
        super.signOut()
    }
}
