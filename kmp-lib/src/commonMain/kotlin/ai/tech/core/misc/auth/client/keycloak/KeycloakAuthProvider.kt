package ai.tech.core.misc.auth.client.keycloak

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.auth.client.ClientAuthProvider
import ai.tech.core.misc.auth.client.keycloak.model.ResetPassword
import ai.tech.core.misc.auth.client.keycloak.model.UserRepresentation
import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.type.single.flatMap
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.datetime.Clock

public class KeycloakAuthProvider(
    public val client: KeycloakClient,
    public val keyValue: AbstractKeyValue,
    public override val name: String?,
) : ClientAuthProvider {

    private var onSignInExpireBlock: (() -> Unit)? = null

    override suspend fun signIn(username: String, password: String) {
        requestToken(username, password)
    }

    override suspend fun signOut() {
        removeToken()
    }

    override suspend fun getUser(): User? = getOrRefreshToken().map { it.token.accessToken }.flatMap { accessToken ->
        client.getUserInfo(accessToken).flatMap(
                { userInfo ->
                    client.getUsers(UserRepresentation(username = userInfo.preferredUsername), true, accessToken)
                            .flatMap { users ->
                                client.getUserRealmRoles(userInfo.sub, accessToken).map {
                                    users.single().copy(realmRoles = it.map { it.name!! }.toSet()).toUser()
                                }
                            }
                },
        ) {
            if (it.isUnauthorized) {
                signOut()
                Result.success(null)
            }
            else {
                Result.failure(it)
            }
        }
    }.getOrNull()

    override suspend fun getUsers(): Set<User> = getOrRefreshToken().flatMap {
        client.getUsers(accessToken = it.token.accessToken).map({ it.map { it.toUser() }.toSet() }).onFailure {
            if (it.isUnauthorized) {
                signOut()
            }
        }
    }.getOrThrow()

    override suspend fun createUsers(users: Set<User>, password: String) {
        getAndRequestToken(password).flatMap { token ->
            kotlin.runCatching {
                users.forEach {
                    client.createUser(it.toUserRepresentation(), token.token.accessToken).getOrThrow()
                }
            }
        }.getOrThrow()
    }

    override suspend fun updateUsers(users: Set<User>, password: String): Unit =
        getAndRequestToken(password).flatMap { token ->
            runCatching {
                users.forEach { user ->
                    client.getUsers(UserRepresentation(username = user.username), true, token.token.accessToken)
                        .map { it.single() }
                        .flatMap {
                            client.updateUser(user.toUserRepresentation().copy(id = it.id), token.token.accessToken)
                        }.getOrThrow()
                }
            }
        }.getOrThrow()

    override suspend fun deleteUsers(usernames: Set<String>, password: String): Unit =
        getAndRequestToken(password).map { it.token.accessToken }.flatMap { accessToken ->
            runCatching {
                usernames.forEach {
                    client.getUsers(UserRepresentation(username = it), true, accessToken).map { it.single() }.flatMap {
                        client.deleteUser(it.id!!, accessToken)
                    }
                }
            }
        }.getOrThrow()

    override suspend fun resetPassword(password: String, newPassword: String) {
        getAndRequestToken(password).flatMap { token ->
            client.getUserInfo(token.token.accessToken).flatMap {
                client.resetPassword(it.sub, ResetPassword(newPassword), token.token.accessToken).flatMap {
                    requestToken(token.username, newPassword)
                }
            }
        }.getOrThrow()
    }

    public override suspend fun forgetPassword(username: String): Unit = client.forgetPassword(username).getOrThrow()

    override suspend fun onSignInExpire(block: () -> Unit) {
        onSignInExpireBlock = block
        handleRefreshTokenExpire()
    }

    override suspend fun configHttpRequest(builder: HttpRequestBuilder) {
        getOrRefreshToken().getOrNull()
            ?.let { builder.header(HttpHeaders.Authorization, "Bearer ${it.token.accessToken}") }
    }

    private suspend fun requestToken(username: String, password: String): Result<CachedToken> =
        client.getToken(username, password).map {
            setToken(username, it)
        }

    private suspend fun getAndRequestToken(password: String): Result<CachedToken> = getOrRefreshToken().flatMap {
        requestToken(it.username, password)
    }

    private suspend fun getOrRefreshToken(): Result<CachedToken> = getToken().flatMap { token ->
        if (token.expiresInLeft > 0) {
            Result.success(token)
        }
        else {
            client.getToken(token.token.refreshToken).map {
                setToken(token.username, it)
            }.onFailure {
                removeToken()
            }
        }
    }

    private suspend fun setToken(username: String, token: Token) =
        CachedToken(Clock.System.now().epochSeconds, username, token).also {
            keyValue.set(TOKEN_KEY, it)
            handleRefreshTokenExpire()
        }

    private suspend fun getToken() = keyValue.get<CachedToken>(TOKEN_KEY)

    private suspend fun removeToken() = keyValue.remove(TOKEN_KEY)

    private suspend fun handleRefreshTokenExpire() {
//        supervisorScope {
//            launch {
//                getToken().map {
//                    it.refreshExpiresInLeft?.let {
//                        delay(it * 1000)
//                        signOut()
//                        onSignInExpireBlock?.invoke()
//                    }
//                }
//            }
//        }
    }

    private val Throwable.isUnauthorized: Boolean
        get() = this is HttpResponseException && this.status == HttpStatusCode.Unauthorized

    private fun UserRepresentation.toUser(): User = User(
        username,
        firstName,
        lastName,
        attributes?.get(USER_PHONE_ATTRIBUTE_KEY)?.get(0),
        email,
        attributes?.get(USER_IMAGE_ATTRIBUTE_KEY)?.get(0),
        realmRoles,
        attributes?.toMutableMap()?.apply {
            remove(USER_PHONE_ATTRIBUTE_KEY)
            remove(USER_IMAGE_ATTRIBUTE_KEY)
        },
    )

    private fun User.toUserRepresentation(): UserRepresentation =
        UserRepresentation(
            username = username,
            firstName = firstName,
            lastName = lastName,
            email = email,
            realmRoles = roles,
            attributes = if (!(phone == null && image == null)) {
                (attributes ?: emptyMap()) + mutableMapOf<String, List<String>>().apply {
                    phone?.let { this[USER_PHONE_ATTRIBUTE_KEY] = listOf(it) }
                    image?.let { this[USER_IMAGE_ATTRIBUTE_KEY] = listOf(it) }
                }
            }
            else {
                attributes
            },
        )

    private companion object {

        private const val TOKEN_KEY = "TOKEN"
        private const val USER_PHONE_ATTRIBUTE_KEY = "phone"
        private const val USER_IMAGE_ATTRIBUTE_KEY = "image"
    }
}
