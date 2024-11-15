package ai.tech.core.misc.auth.client.keycloak

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
import io.ktor.client.HttpClient
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.datetime.Clock

public class KeycloakService(
    private val httpClient: HttpClient,
    public val config: ClientOAuthConfig,
    public val keyValue: AbstractKeyValue,
) : ClientAuthService {

    private val tokenClient = KeycloakTokenClient(httpClient, config)
    private lateinit var adminClient: KeycloakAdminClient

    private val lock = reentrantLock()

    override suspend fun signIn(username: String, password: String): Unit = lock.withLock {
        setToken(tokenClient.getToken(username, password))
    }

    override suspend fun signOut(): Unit = lock.withLock {
        removeToken()
    }

    override suspend fun getUser(): User? = lock.withLock {
        getOrRefreshToken()

        if (adminClient == null) return null

        val userId = adminClient.getUserInfo().let(UserInfo::sub)

        return adminClient.getUsers(UserRepresentation(id = userId), true).singleOrNull()?.let {

            val roles = adminClient.getUserRealmRoles(userId)

            it.copy(realmRoles = roles.map { it.name!! }.toSet()).toUser()
        }
    }

    override suspend fun getUsers(): Set<User> = lock.withLock {
        getOrRefreshToken()

        return adminClient.getUsers().map(UserRepresentation::toUser).toSet()
    }

    override suspend fun createUsers(users: Set<User>, password: String): Unit = lock.withLock {
        getOrRefreshToken()

        users.forEach { adminClient.createUser(UserRepresentation(it)) }
    }

    override suspend fun updateUsers(users: Set<User>, password: String): Unit = lock.withLock {
        getOrRefreshToken()

        users.forEach { user ->
            val userId = adminClient.getUsers(UserRepresentation(username = user.username), true).single().let(UserRepresentation::id)

            adminClient.updateUser(UserRepresentation(user, userId))
        }
    }

    override suspend fun deleteUsers(usernames: Set<String>, password: String): Unit = lock.withLock {
        getOrRefreshToken()

        usernames.forEach {
            val userId = adminClient.getUsers(UserRepresentation(username = it), true).single().let(UserRepresentation::id)!!

            adminClient.deleteUser(userId)
        }
    }

    override suspend fun resetPassword(password: String, newPassword: String): Unit = lock.withLock {
        getOrRefreshToken()

        val userId = adminClient.getUserInfo().let(UserInfo::sub)

        adminClient.resetPassword(userId, ResetPassword(newPassword))
    }

    public override suspend fun forgotPassword(username: String): Unit = lock.withLock {
        getOrRefreshToken()

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
        getOrRefreshToken()?.let {
            httpRequestBuilder.header(HttpHeaders.Authorization, "Bearer ${it.accessToken}")
        }
    }

    private suspend fun getOrRefreshToken(): TokenResponse? =
        getToken()?.let {
            val passedEpochSeconds: Long = Clock.System.now().epochSeconds - keyValue.get<Long>(TOKEN_EPOCH_SECONDS_KEY)

            if (passedEpochSeconds > it.expiresIn) {
                if (it.refreshExpiresIn != null && passedEpochSeconds > it.refreshExpiresIn) {
                    removeToken()
                    return null
                }
                return tokenClient.getTokenByRefreshToken(it.refreshToken).also { setToken(it) }
            }
            it
        }

    private suspend fun setToken(token: TokenResponse) {
        keyValue.set(TOKEN_KEY, token)

        keyValue.set(TOKEN_EPOCH_SECONDS_KEY, Clock.System.now().epochSeconds)

        adminClient = KeycloakAdminClient(httpClient, config, token.accessToken)
    }

    private suspend fun getToken() = keyValue.get<TokenResponse?>(TOKEN_KEY)

    private suspend fun removeToken() = keyValue.remove(TOKEN_KEY)

    public companion object {

        public const val TOKEN_KEY: String = "KEYCLOAK_TOKEN"
        public const val TOKEN_EPOCH_SECONDS_KEY: String = "${TOKEN_KEY}_KEY_EPOCH_SECONDS_KEY"
        public const val REFRESH_TOKEN_EPOCH_SECONDS_KEY: String = "${TOKEN_KEY}_EPOCH_SECONDS_KEY"
    }
}
