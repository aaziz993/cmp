package ai.tech.core.misc.auth.client

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.data.keyvalue.get
import ai.tech.core.misc.auth.model.bearer.Token
import ai.tech.core.misc.auth.model.bearer.TokenImpl
import ai.tech.core.misc.network.http.client.apiClient
import ai.tech.core.misc.network.http.client.httpUrl
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.authProvider
import io.ktor.client.plugins.auth.providers.BearerAuthProvider
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.request.forms.submitForm
import io.ktor.http.parameters
import kotlinx.datetime.Clock.System

public abstract class ClientBearerAuthService(
    httpClient: HttpClient,
    override val name: String,
    public val address: String,
    public val tokenUri: String,
    public val clientId: String,
    public val keyValue: AbstractKeyValue
) : ClientAuthService {

    private val tokenKey = "${name}_token"
    private val tokenEpochSecondsKey = "${name}_token_epoch_seconds"

    public val httpClient: HttpClient = httpClient.apiClient {
        install(Auth) {
            bearer {
                loadTokens { getToken()?.let { BearerTokens(it.accessToken, it.refreshToken) } }

                refreshTokens {
                    val token: TokenImpl = client.submitForm(
                        url = "$address/$tokenUri",
                        formParameters = parameters {
                            append("grant_type", "refresh_token")
                            append("client_id", clientId)
                            append("refresh_token", oldTokens?.refreshToken.orEmpty())
                        },
                    ) { markAsRefreshTokenRequest() }.body()

                    setToken(token)

                    BearerTokens(token.accessToken, oldTokens?.refreshToken!!)
                }

                sendWithoutRequest { request ->
                    request.url.host == address.httpUrl.host
                }
            }
        }
    }

    protected abstract suspend fun getToken(username: String, password: String): Token

    final override suspend fun authIn(username: String, password: String): Unit =
        setToken(getToken(username, password))

    protected abstract suspend fun getTokenByRefreshToken(refreshToken: String): Token

    public suspend fun authByRefreshToken(refreshToken: String): Unit =
        setToken(getTokenByRefreshToken(refreshToken))

    protected abstract suspend fun getTokenByClientSecret(clientSecret: String): Token

    public suspend fun authByClientSecret(clientSecret: String): Unit =
        setToken(getTokenByClientSecret(clientSecret))

    final override suspend fun authOut() {
        removeToken()
        httpClient.authProvider<BearerAuthProvider>()?.clearToken()
    }

    final override suspend fun isAuth(): Boolean =
        getToken()?.let {
            val passedEpochSeconds: Long = System.now().epochSeconds - keyValue.get<Long>(tokenEpochSecondsKey)

            it.refreshExpiresIn == null || passedEpochSeconds < it.refreshExpiresIn
        } == true

    protected suspend fun setToken(token: Token): Unit = keyValue.transactional {
        set(tokenKey, token)

        set(tokenEpochSecondsKey, System.now().epochSeconds)
    }

    protected suspend fun getToken(): TokenImpl? = keyValue.transactional { get(tokenKey) }

    protected suspend fun removeToken(): Unit = keyValue.transactional { remove(tokenKey) }
}
