package ai.tech.core.misc.auth.client

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.data.keyvalue.get
import ai.tech.core.misc.auth.model.bearer.BearerToken
import ai.tech.core.misc.auth.model.bearer.BearerTokenImpl
import ai.tech.core.misc.network.http.client.httpUrl
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.forms.submitForm
import io.ktor.http.ParametersBuilder
import io.ktor.http.parameters

public abstract class BearerAuthService(
    override val name: String,
    httpClient: HttpClient,
    address: String,
    public val tokenUri: String,
    public val keyValue: AbstractKeyValue
) : AuthService {

    public val authHttpClient: HttpClient = httpClient.config {
        install(Auth) {
            bearer {
                loadTokens { getToken()?.let { BearerTokens(it.token, it.refreshToken) } }

                refreshTokens {
                    val token = getTokenByRefreshToken()

                    setToken(token)

                    BearerTokens(token.token, oldTokens?.refreshToken!!)
                }

                sendWithoutRequest { request ->
                    request.url.host == address.httpUrl.host
                }
            }
        }
    }

    private val tokenKey = "${name}_token"

    protected abstract suspend fun RefreshTokensParams.getTokenByRefreshToken(): BearerToken

    protected abstract suspend fun getToken(username: String, password: String): BearerToken


    final override suspend fun signIn(username: String, password: String): Unit =
        setToken(getToken(username, password))

    final override suspend fun signOut() {
        removeToken()
        authHttpClient.authProvider<BearerAuthProvider>()?.clearToken()
    }

    protected suspend fun setToken(token: BearerToken): Unit = keyValue.transactional {
        set(tokenKey, token)
    }

    protected suspend fun getToken(): BearerTokenImpl? = keyValue.transactional { get(tokenKey) }

    protected suspend fun removeToken(): Unit = keyValue.transactional { remove(tokenKey) }
}
