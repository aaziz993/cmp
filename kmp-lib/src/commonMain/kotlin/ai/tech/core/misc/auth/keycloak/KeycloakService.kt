package ai.tech.core.misc.auth.keycloak

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.data.keyvalue.get
import ai.tech.core.misc.auth.client.ClientAuthService
import ai.tech.core.misc.auth.client.keycloak.admin.KeycloakAdminClient
import ai.tech.core.misc.auth.client.keycloak.admin.model.ExecuteActionsEmail
import ai.tech.core.misc.auth.client.keycloak.admin.model.ResetPassword
import ai.tech.core.misc.auth.client.keycloak.token.model.TokenResponse
import ai.tech.core.misc.auth.client.keycloak.admin.model.UserInfo
import ai.tech.core.misc.auth.client.keycloak.admin.model.UserRepresentation
import ai.tech.core.misc.auth.client.keycloak.token.KeycloakTokenClient
import ai.tech.core.misc.auth.client.model.config.oauth.ClientOAuthConfig
import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.network.http.client.apiClient
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.RefreshTokensParams
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.json
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.datetime.Clock
import kotlinx.serialization.json.Json

public class KeycloakService(
    private val httpClient: HttpClient,
    public val config: ClientOAuthConfig,
    public val keyValue: AbstractKeyValue,
) : ClientAuthService {

    private val tokenClient = KeycloakTokenClient(
        Ktorfit.Builder().httpClient(httpClient.apiClient()).baseUrl(config.address).build(),
        config.realm,
        config.clientId,
    )

    private var adminClient: KeycloakAdminClient = KeycloakAdminClient(
        Ktorfit.Builder().httpClient(
            httpClient.apiClient {
                install(Auth) {
                    bearer {
                        loadTokens { getToken()?.let { BearerTokens(it.accessToken, it.refreshToken) } }

                        refreshTokens { refreshToken() }
                    }
                }
            },
        ).baseUrl(config.address).build(),
        config.realm,
    )

    private val lock = reentrantLock()

    override suspend fun signIn(username: String, password: String): Unit = lock.withLock {
        setToken(tokenClient.getToken(username, password))
    }

    override suspend fun signOut(): Unit = lock.withLock {
        removeToken()
    }

    override suspend fun getUser(): User? = lock.withLock {
        val userId = adminClient.getUserInfo().let(UserInfo::sub)

        adminClient.getUsers(UserRepresentation(id = userId), true).singleOrNull()?.let {
            val roles = adminClient.getUserRealmRoles(userId)

            it.copy(realmRoles = roles.map { it.name!! }.toSet()).toUser()
        }
    }

    override suspend fun getUsers(): Set<User> = lock.withLock {
        adminClient.getUsers().map(UserRepresentation::toUser).toSet()
    }

    override suspend fun createUsers(users: Set<User>, password: String): Unit = lock.withLock {
        users.forEach { adminClient.createUser(UserRepresentation(it)) }
    }

    override suspend fun updateUsers(users: Set<User>, password: String): Unit = lock.withLock {
        users.forEach { user ->
            val userId = adminClient.getUsers(UserRepresentation(username = user.username), true).single().let(UserRepresentation::id)

            adminClient.updateUser(UserRepresentation(user, userId))
        }
    }

    override suspend fun deleteUsers(usernames: Set<String>, password: String): Unit = lock.withLock {
        usernames.forEach {
            val userId = adminClient.getUsers(UserRepresentation(username = it), true).single().let(UserRepresentation::id)!!

            adminClient.deleteUser(userId)
        }
    }

    override suspend fun resetPassword(password: String, newPassword: String): Unit = lock.withLock {
        val userId = adminClient.getUserInfo().let(UserInfo::sub)

        adminClient.resetPassword(userId, ResetPassword(newPassword))
    }

    public override suspend fun forgotPassword(username: String): Unit = lock.withLock {
        val userId = adminClient.getUserInfo().let(UserInfo::sub)

        adminClient.executeActionsEmail(userId, ExecuteActionsEmail(listOf("UPDATE_PASSWORD")))
    }

    override suspend fun isSignedIn(): Boolean = lock.withLock {
        getToken()?.let {
            val passedEpochSeconds: Long = Clock.System.now().epochSeconds - keyValue.get<Long>(TOKEN_EPOCH_SECONDS_KEY)

            it.refreshExpiresIn == null || passedEpochSeconds <= it.refreshExpiresIn
        } == true
    }

    override suspend fun auth(httpRequestBuilder: HttpRequestBuilder): Unit = lock.withLock {
        getToken()?.let {
            httpRequestBuilder.header(HttpHeaders.Authorization, "Bearer ${it.accessToken}")
        }
    }

    private suspend fun RefreshTokensParams.refreshToken(): BearerTokens? =
        getToken()?.let { token ->
            val passedEpochSeconds: Long = Clock.System.now().epochSeconds - keyValue.get<Long>(TOKEN_EPOCH_SECONDS_KEY)

            if (token.refreshExpiresIn == null || passedEpochSeconds < token.refreshExpiresIn) {
                val newToken = tokenClient.getTokenByRefreshToken(token.refreshToken)
                setToken(newToken)
                return BearerTokens(newToken.accessToken, newToken.refreshToken)
            }

            removeToken()

            null
        }

    private suspend fun setToken(token: TokenResponse) {
        keyValue.set(TOKEN_KEY, token)

        keyValue.set(TOKEN_EPOCH_SECONDS_KEY, Clock.System.now().epochSeconds)
    }

    private suspend fun getToken() = keyValue.get<TokenResponse?>(TOKEN_KEY)

    private suspend fun removeToken() = keyValue.remove(TOKEN_KEY)

    public companion object {

        public const val TOKEN_KEY: String = "KEYCLOAK_TOKEN"
        public const val TOKEN_EPOCH_SECONDS_KEY: String = "${TOKEN_KEY}_KEY_EPOCH_SECONDS_KEY"
    }
}
