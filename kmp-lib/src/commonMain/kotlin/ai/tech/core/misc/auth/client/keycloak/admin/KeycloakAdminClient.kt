package ai.tech.core.misc.auth.client.keycloak.admin

import ai.tech.core.misc.auth.client.keycloak.createKeycloakAdminApi
import ai.tech.core.misc.auth.client.keycloak.admin.model.ExecuteActionsEmail
import ai.tech.core.misc.auth.client.keycloak.admin.model.ResetPassword
import ai.tech.core.misc.auth.client.keycloak.admin.model.RoleRepresentation
import ai.tech.core.misc.auth.client.keycloak.admin.model.UserInfo
import ai.tech.core.misc.auth.client.keycloak.admin.model.UserRepresentation
import ai.tech.core.misc.auth.client.model.config.oauth.ClientOAuthConfig
import ai.tech.core.misc.type.serializer.encodeAnyToString
import ai.tech.core.misc.type.serializer.encodeToAny
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders
import io.ktor.serialization.kotlinx.json.json
import kotlin.collections.orEmpty
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

public class KeycloakAdminClient(
    httpClient: HttpClient,
    public val config: ClientOAuthConfig,
    private val accessToken: String,
) {

    private val ktorfit = Ktorfit.Builder().httpClient(
        httpClient.config {
            defaultRequest {
                header(HttpHeaders.Authorization, "Bearer $accessToken")
            }

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

    private val api = ktorfit.createKeycloakAdminApi()

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    public suspend fun createUser(userRepresentation: UserRepresentation): Unit = api.createUser(config.realm, userRepresentation)

    public suspend fun getUsers(userRepresentation: UserRepresentation? = null, exact: Boolean? = null): Set<UserRepresentation> =
        api.getUsers(
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

    public suspend fun updateUser(userRepresentation: UserRepresentation): Unit =
        api.updateUser(config.realm, userRepresentation.id!!, userRepresentation)

    public suspend fun deleteUser(userId: String): Unit = api.deleteUser(config.realm, userId)

    public suspend fun getUserInfo(): UserInfo = api.getUserInfo(config.realm)

    public suspend fun getUserRealmRoles(userId: String): Set<RoleRepresentation> =
        api.getUserRealmRoles(config.realm, userId)

    public suspend fun resetPassword(userId: String, resetPassword: ResetPassword): Unit =
        api.resetPassword(config.realm, userId, resetPassword)

    public suspend fun executeActionsEmail(userId: String, executeActionsEmail: ExecuteActionsEmail): Unit =
        api.updatePassword(config.realm, userId, executeActionsEmail)
}
