package ai.tech.core.misc.auth.keycloak

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.auth.client.ClientBearerAuthService
import ai.tech.core.misc.auth.client.model.config.oauth.ClientOAuthConfig
import ai.tech.core.misc.auth.keycloak.client.admin.KeycloakAdminClient
import ai.tech.core.misc.auth.keycloak.client.admin.model.ExecuteActionsEmail
import ai.tech.core.misc.auth.keycloak.client.admin.model.ResetPassword
import ai.tech.core.misc.auth.keycloak.client.admin.model.UserInfo
import ai.tech.core.misc.auth.keycloak.client.admin.model.UserRepresentation
import ai.tech.core.misc.auth.keycloak.client.token.KeycloakTokenClient
import ai.tech.core.misc.auth.model.User
import ai.tech.core.misc.auth.model.bearer.Token
import ai.tech.core.misc.network.http.client.configApi
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient

public class KeycloakService(
    httpClient: HttpClient,
    name: String,
    public val config: ClientOAuthConfig,
    keyValue: AbstractKeyValue,
) : ClientBearerAuthService(
    httpClient,
    name,
    config.address,
    "realms/{realm}/protocol/openid-connect/token",
    config.clientId,
    keyValue,
) {

    private val tokenClient = KeycloakTokenClient(
        Ktorfit.Builder().httpClient(httpClient.configApi()).baseUrl(config.address).build(),
        config.realm,
        config.clientId,
    )

    private val adminClient: KeycloakAdminClient = KeycloakAdminClient(
        Ktorfit.Builder().httpClient(httpClient).baseUrl(config.address).build(),
        config.realm,
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
