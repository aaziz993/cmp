package ai.tech.core.misc.auth.client

import ai.tech.core.data.keyvalue.AbstractKeyValue
import io.ktor.client.*
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.AuthConfig
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.serialization.DeserializationStrategy

public abstract class AbstractAuthService<T : Any>(
    public val name: String?,
    httpClient: HttpClient,
    private val deserializer: DeserializationStrategy<T>,
    private val keyValue: AbstractKeyValue,
) {

    private val tokenKey = "${name?.let { "${it}_" }.orEmpty()}token"

    public val signOutFlow: SharedFlow<Unit>
        field = MutableSharedFlow()

    public val httpClient: HttpClient = httpClient.config {
        install(Auth) {
            configureAuth()
        }
    }

    protected abstract fun AuthConfig.configureAuth()

    public open suspend fun signOut() {
        removeToken()
        signOutFlow.emit(Unit)
    }

    protected suspend fun setToken(value: T): Unit = keyValue.transactional { set(tokenKey, value) }

    protected suspend fun getToken(): T? = keyValue.transactional { get(tokenKey, deserializer) }

    private suspend fun removeToken(): Unit = keyValue.transactional { remove(tokenKey) }
}
