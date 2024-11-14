package ai.tech.core.misc.auth.client.keycloak

import ai.tech.core.misc.auth.client.keycloak.model.ExecuteActionsEmail
import ai.tech.core.misc.auth.client.keycloak.model.ResetPassword
import ai.tech.core.misc.auth.client.keycloak.model.RoleRepresentation
import ai.tech.core.misc.auth.client.keycloak.model.TokenResponse
import ai.tech.core.misc.auth.client.keycloak.model.UserInfo
import ai.tech.core.misc.auth.client.keycloak.model.UserRepresentation
import ai.tech.core.misc.auth.client.model.config.oauth.ClientOAuthConfig
import ai.tech.core.misc.type.encodeAnyToString
import ai.tech.core.misc.type.encodeToAny
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

public class KeycloakClient(
    httpClient: HttpClient,
    public val config: ClientOAuthConfig,
) {

    private val ktorfit = Ktorfit.Builder().httpClient(
        httpClient.config {

            install(ContentNegotiation) {
                json(
                    Json {
                        isLenient = true
                        ignoreUnknownKeys = true
                        explicitNulls = false
                    },
                )
            }
        },
    ).baseUrl(config.address).build()

    private val api = ktorfit.createKeycloakApi()

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    public suspend fun getToken(username: String, password: String): TokenResponse =
        api.getToken(config.realm, username, password, config.clientId)

    public suspend fun getTokenByRefreshToken(refreshToken: String): TokenResponse =
        api.getTokenByRefreshToken(config.realm, refreshToken, config.clientId)

    public suspend fun getTokenByClientSecret(clientSecret: String): TokenResponse =
        api.getTokenByClientSecret(config.realm, clientSecret, config.clientId)

    public suspend fun createUser(
        accessToken: String,
        userRepresentation: UserRepresentation,
    ): Unit = api.createUser("Bearer $accessToken", config.realm, userRepresentation)

    public suspend fun getUsers(
        accessToken: String,
        userRepresentation: UserRepresentation? = null,
        exact: Boolean? = null,
    ): Set<UserRepresentation> = api.getUsers(
        "Bearer $accessToken",
        config.realm,
        userRepresentation?.let {
            listOfNotNull(
                exact?.let { "exact" to it.toString() },
                (it.attributes as Map<*, *>?)?.let {
                    "q" to it.entries.joinToString(" ") { (k, v) -> "$k:${json.encodeAnyToString(v)}" }
                },
            ) + (json.encodeToAny(it) as Map<*, *>).filter { (k, v) -> k !== "attributes" && v != null }.map { (k, v) ->
                k.toString() to json.encodeAnyToString(v)
            }
        }?.toMap().orEmpty(),
    )

    public suspend fun updateUser(
        accessToken: String,
        userRepresentation: UserRepresentation
    ): Unit = api.updateUser("Bearer $accessToken", config.realm, userRepresentation.id!!, userRepresentation)

    public suspend fun deleteUser(
        accessToken: String,
        userId: String,
    ): Unit = api.deleteUser("Bearer $accessToken", config.realm, userId)

    public suspend fun getUserInfo(accessToken: String): UserInfo =
        api.getUserInfo("Bearer $accessToken", config.realm)

    public suspend fun getUserRealmRoles(
        accessToken: String,
        userId: String,
    ): Set<RoleRepresentation> = api.getUserRealmRoles("Bearer $accessToken", config.realm, userId)

    public suspend fun resetPassword(
        accessToken: String,
        userId: String,
        resetPassword: ResetPassword
    ): Unit = api.resetPassword("Bearer $accessToken", config.realm, userId, resetPassword)

    public suspend fun executeActionsEmail(
        accessToken: String,
        userId: String,
        executeActionsEmail: ExecuteActionsEmail
    ): Unit = api.updatePassword("Bearer $accessToken", config.realm, userId, executeActionsEmail)
}
