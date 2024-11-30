package ai.tech.core.misc.auth.keycloak

import ai.tech.core.data.keyvalue.AbstractKeyValue
import ai.tech.core.misc.auth.client.BearerAuthService
import ai.tech.core.misc.auth.keycloak.client.admin.KeycloakAdminClient
import ai.tech.core.misc.auth.keycloak.client.admin.model.ExecuteActionsEmail
import ai.tech.core.misc.auth.keycloak.client.admin.model.ResetPassword
import ai.tech.core.misc.auth.keycloak.client.admin.model.UserInfo
import ai.tech.core.misc.auth.keycloak.client.admin.model.UserRepresentation
import ai.tech.core.misc.auth.keycloak.client.token.KeycloakTokenClient
import ai.tech.core.misc.auth.model.identity.User
import ai.tech.core.misc.auth.model.bearer.BearerToken
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.plugins.auth.providers.RefreshTokensParams
import io.ktor.client.request.forms.submitForm
import io.ktor.http.ParametersBuilder
import io.ktor.http.parameters
import kotlin.text.append

public class KeycloakService(
    name: String,
    httpClient: HttpClient,
    address: String,
    public val realm: String,
    public val clientId: String,
    keyValue: AbstractKeyValue,
) : BearerAuthService(
    name,
    httpClient,
    address,
    "realms/{realm}/protocol/openid-connect/token",
    keyValue,
) {

    private val tokenClient = KeycloakTokenClient(
        httpClient,
        address,
        realm,
        clientId,
    )

    private val adminClient = KeycloakAdminClient(
        authHttpClient,
        address,
        realm,
    )

    override suspend fun RefreshTokensParams.getTokenByRefreshToken(): BearerToken {
        client.submitForm(
            url = "$address/$tokenUri",
            formParameters = parameters {
                append("grant_type", "refresh_token")
                append("client_id", clientId)
                append("refresh_token", oldTokens?.refreshToken.orEmpty())
            },
        ) { markAsRefreshTokenRequest() }.body()
    }


    override suspend fun getToken(username: String, password: String): BearerToken = tokenClient.getToken(username, password)

    override suspend fun getTokenByRefreshToken(refreshToken: String): BearerToken = tokenClient.getTokenByRefreshToken(refreshToken)

    public suspend fun getTokenByClientSecret(clientSecret: String): BearerToken = tokenClient.getTokenByClientSecret(clientSecret)

    override suspend fun getUser(): User? {
        val userId = adminClient.getUserInfo().let(UserInfo::sub)

        return adminClient.getUsers(UserRepresentation(id = userId), true).singleOrNull()?.let {
            val roles = adminClient.getUserRealmRoles(userId)

            it.copy(realmRoles = roles.map { it.name!! }.toSet()).asUser
        }
    }

    override suspend fun getUsers(): Set<User> =
        adminClient.getUsers().map(UserRepresentation::asUser).toSet()

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
