package ai.tech.core.misc.auth.keycloak

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.auth.client.ClientBearerAuthService
import ai.tech.core.misc.auth.keycloak.client.admin.KeycloakAdminClient
import ai.tech.core.misc.auth.keycloak.client.admin.model.ExecuteActionsEmail
import ai.tech.core.misc.auth.keycloak.client.admin.model.ResetPassword
import ai.tech.core.misc.auth.keycloak.client.admin.model.UserInfo
import ai.tech.core.misc.auth.keycloak.client.admin.model.UserRepresentation
import ai.tech.core.misc.auth.keycloak.client.token.KeycloakTokenClient
import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.auth.model.bearer.Token
import ai.tech.core.misc.auth.model.bearer.TokenImpl
import ai.tech.core.misc.network.http.client.httpUrl
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.auth.*
import io.ktor.client.plugins.auth.providers.*
import io.ktor.client.request.forms.*
import io.ktor.http.*

public class KeycloakService(
    name: String,
    httpClient: HttpClient,
    public val address: String,
    public val realm: String,
    clientId: String,
    keyValue: AbstractKeyValue,
) : ClientBearerAuthService(
    name,
    "realms/{realm}/protocol/openid-connect/token",
    clientId,
    keyValue,
) {

    override val authHttpClient: HttpClient = httpClient.config {
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

    private val tokenClient = KeycloakTokenClient(
        httpClient,
        address,
        realm,
        clientId,
    )

    private val adminClient: KeycloakAdminClient = KeycloakAdminClient(
        authHttpClient,
        address,
        realm,
    )

    override suspend fun getToken(username: String, password: String): Token = tokenClient.getToken(username, password)

    override suspend fun getTokenByRefreshToken(refreshToken: String): Token = tokenClient.getTokenByRefreshToken(refreshToken)

    override suspend fun getTokenByClientSecret(clientSecret: String): Token = tokenClient.getTokenByClientSecret(clientSecret)

    override suspend fun getUser(): User? {
        val userId = adminClient.getUserInfo().let(UserInfo::sub)

        return adminClient.getUsers(UserRepresentation(id = userId), true).singleOrNull()?.let {
            val roles = adminClient.getUserRealmRoles(userId)

            it.copy(realmRoles = roles.map { it.name!! }.toSet()).toUser()
        }
    }

    override suspend fun getUsers(): Set<User> =
        adminClient.getUsers().map(UserRepresentation::toUser).toSet()

    override suspend fun createUsers(users: Set<User>, password: String): Unit =
        users.forEach { adminClient.createUser(UserRepresentation(it)) }

    override suspend fun updateUsers(users: Set<User>, password: String): Unit =
        users.forEach { user ->
            val userId = adminClient.getUsers(UserRepresentation(username = user.username), true).single().let(UserRepresentation::id)

            adminClient.updateUser(UserRepresentation(user, userId))
        }

    override suspend fun deleteUsers(usernames: Set<String>, password: String): Unit =
        usernames.forEach {
            val userId = adminClient.getUsers(UserRepresentation(username = it), true).single().let(UserRepresentation::id)!!

            adminClient.deleteUser(userId)
        }

    override suspend fun resetPassword(password: String, newPassword: String) {
        val userId = adminClient.getUserInfo().let(UserInfo::sub)

        adminClient.resetPassword(userId, ResetPassword(newPassword))
    }

    public override suspend fun forgotPassword(): Unit =
        executeActionsEmail(ExecuteActionsEmail(listOf("UPDATE_PASSWORD")))

    public suspend fun executeActionsEmail(executeActionsEmail: ExecuteActionsEmail) {
        val userId = adminClient.getUserInfo().let(UserInfo::sub)

        adminClient.executeActionsEmail(userId, executeActionsEmail)
    }
}
