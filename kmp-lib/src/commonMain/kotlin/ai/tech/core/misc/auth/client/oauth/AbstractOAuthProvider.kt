package ai.tech.core.misc.auth.client.oauth

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.auth.client.bearer.AbstractBearerAuthProvider
import ai.tech.core.misc.auth.client.bearer.model.BearerToken
import ai.tech.core.misc.auth.client.oauth.model.AuthenticationFailedCause
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.providers.RefreshTokensParams
import io.ktor.http.*
import io.ktor.util.logging.KtorSimpleLogger
import io.ktor.util.logging.Logger
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine

private val Logger: Logger = KtorSimpleLogger("io.ktor.auth.oauth")

public abstract class AbstractOAuthProvider<T : BearerToken>(
    name: String?,
    httpClient: HttpClient,
    public val callbackRedirectUrl: String,
    keyValue: AbstractKeyValue,
    protected val onRedirectAuthenticate: suspend (url: Url) -> Unit
) : AbstractBearerAuthProvider(
    name,
    httpClient,
    keyValue,
) {

    protected var continuation: CancellableContinuation<T>? = null

    protected abstract suspend fun getRedirectUrl(): Url

    public suspend fun signIn(): Unit = setToken(requestToken())

    protected suspend fun requestToken(): T {
        onRedirectAuthenticate(getRedirectUrl())

        val accessToken = suspendCancellableCoroutine<T> { continuation ->
            this@AbstractOAuthProvider.continuation = continuation
        }

        continuation = null

        return accessToken
    }

    public abstract suspend fun callback(parameters: Parameters): AuthenticationFailedCause?

    override suspend fun RefreshTokensParams.refreshToken(): BearerToken? = null

    protected fun resume(token: T) {
        continuation?.resume(token) { cause, _, _ -> Logger.warn("Callback canceled", cause) }
    }
}
