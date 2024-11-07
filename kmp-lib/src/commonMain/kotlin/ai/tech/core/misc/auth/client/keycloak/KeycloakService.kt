package ai.tech.core.misc.auth.client.keycloak

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.data.keyvalue.get
import ai.tech.core.misc.auth.client.ClientAuthService
import ai.tech.core.misc.auth.client.keycloak.model.ResetPassword
import ai.tech.core.misc.auth.client.keycloak.model.TokenResponse
import ai.tech.core.misc.auth.client.keycloak.model.UserInfo
import ai.tech.core.misc.auth.client.keycloak.model.UserRepresentation
import ai.tech.core.misc.auth.model.User
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.datetime.Clock

public class KeycloakService(
    public val client: KeycloakClient,
    public val keyValue: AbstractKeyValue,
) : ClientAuthService {

    private val lock = reentrantLock()

    override suspend fun signIn(username: String, password: String): Unit = setToken(client.getToken(username, password))

    override suspend fun signOut(): Unit = removeToken()

    override suspend fun getUser(): User? = getOrRefreshToken()?.let(TokenResponse::accessToken)?.let { accessToken ->

        val userId = client.getUserInfo(accessToken).let(UserInfo::sub)

        return client.getUsers(UserRepresentation(id = userId), true, accessToken).singleOrNull()?.let {

            val roles = client.getUserRealmRoles(userId, accessToken)

            it.copy(realmRoles = roles.map { it.name!! }.toSet()).toUser()
        }
    }

    override suspend fun getUsers(): Set<User> = getOrRefreshToken()!!.let(TokenResponse::accessToken).let {
        client.getUsers(accessToken = it).map(UserRepresentation::toUser).toSet()
    }

    override suspend fun createUsers(users: Set<User>, password: String): Unit = getOrRefreshToken()!!.let(TokenResponse::accessToken).let { accessToken ->
        users.forEach {
            client.createUser(UserRepresentation(it), accessToken)
        }
    }

    override suspend fun updateUsers(users: Set<User>, password: String): Unit = getOrRefreshToken()!!.let(TokenResponse::accessToken).let { accessToken ->
        users.forEach { user ->
            val userId = client.getUsers(UserRepresentation(username = user.username), true, accessToken).single().let(UserRepresentation::id)

            client.updateUser(UserRepresentation(user, userId), accessToken)
        }
    }

    override suspend fun deleteUsers(usernames: Set<String>, password: String): Unit = getOrRefreshToken()!!.let(TokenResponse::accessToken).let { accessToken ->
        usernames.forEach {
            val userId = client.getUsers(UserRepresentation(username = it), true, accessToken).single().let(UserRepresentation::id)!!

            client.deleteUser(userId, accessToken)
        }
    }

    override suspend fun resetPassword(password: String, newPassword: String): Unit = getOrRefreshToken()!!.let(TokenResponse::accessToken).let { accessToken ->
        val userId = client.getUserInfo(accessToken).let(UserInfo::sub)

        client.resetPassword(userId, ResetPassword(newPassword), accessToken)
    }

    public override suspend fun forgotPassword(username: String): Unit = getOrRefreshToken()!!.let(TokenResponse::accessToken).let { accessToken ->
        val userId = client.getUserInfo(accessToken).let(UserInfo::sub)

        client.updatePassword(userId, accessToken)
    }

    override suspend fun isValidToken(): Boolean = getToken()?.let {
        val passedEpochSeconds: Long = Clock.System.now().epochSeconds - keyValue.get<Long>(TOKEN_EPOCH_SECONDS_KEY)

        it.refreshExpiresIn == null || passedEpochSeconds <= it.refreshExpiresIn
    } == true

    override suspend fun HttpRequestBuilder.auth() {
        getOrRefreshToken()?.let {
            header(HttpHeaders.Authorization, "Bearer ${it.accessToken}")
        }
    }

    private suspend fun getOrRefreshToken(): TokenResponse? = lock.withLock {
        getToken()?.let {
            val passedEpochSeconds: Long = Clock.System.now().epochSeconds - keyValue.get<Long>(TOKEN_EPOCH_SECONDS_KEY)

            if (passedEpochSeconds > it.expiresIn) {
                if (it.refreshExpiresIn != null && passedEpochSeconds > it.refreshExpiresIn) {
                    removeToken()
                    return@withLock null
                }

                return client.getTokenByRefreshToken(it.refreshToken).also {
                    setToken(it)
                }
            }

            it
        }
    }

    private suspend fun setToken(token: TokenResponse) = lock.withLock {
        keyValue.set(TOKEN_KEY, token)

        val epochSeconds = Clock.System.now().epochSeconds

        keyValue.set(TOKEN_EPOCH_SECONDS_KEY, epochSeconds)
    }

    private suspend fun getToken() = keyValue.get<TokenResponse?>(TOKEN_KEY)

    private suspend fun removeToken() = lock.withLock {
        keyValue.remove(TOKEN_KEY)
    }

    public companion object {

        public const val TOKEN_KEY: String = "KEYCLOAK_TOKEN"
        public const val TOKEN_EPOCH_SECONDS_KEY: String = "${TOKEN_KEY}_KEY_EPOCH_SECONDS_KEY"
        public const val REFRESH_TOKEN_EPOCH_SECONDS_KEY: String = "${TOKEN_KEY}_EPOCH_SECONDS_KEY"
    }
}
