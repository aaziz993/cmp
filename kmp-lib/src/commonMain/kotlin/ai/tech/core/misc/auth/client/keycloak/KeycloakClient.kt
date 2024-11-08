package ai.tech.core.misc.auth.client.keycloak

import ai.tech.core.misc.auth.client.keycloak.model.ResetPassword
import ai.tech.core.misc.auth.client.keycloak.model.RoleRepresentation
import ai.tech.core.misc.auth.client.keycloak.model.TokenResponse
import ai.tech.core.misc.auth.client.keycloak.model.UpdatePassword
import ai.tech.core.misc.auth.client.keycloak.model.UserInfo
import ai.tech.core.misc.auth.client.keycloak.model.UserRepresentation
import ai.tech.core.misc.auth.client.model.config.oauth.ClientOAuthConfig
import ai.tech.core.misc.type.encode
import ai.tech.core.misc.type.encodeToAny
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

public class KeycloakClient(
    httpClient: HttpClient,
    public val config: ClientOAuthConfig,
) {

    @OptIn(ExperimentalSerializationApi::class)
    private val httpClient: HttpClient = httpClient.config {
        defaultRequest {
            url(config.address)
        }

        install(ContentNegotiation) {
            json(
                Json {
                    prettyPrint = true
                    isLenient = true
                    ignoreUnknownKeys = true
                    explicitNulls = false
                },
            )
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    public suspend fun getToken(username: String, password: String): TokenResponse =
        httpClient.submitForm(
            "/realms/${config.realm}/protocol/openid-connect/token",
            parameters {
                append("username", username)
                append("password", password)
                append("client_id", config.clientId)
                append("grant_type", "password")
            },
        ).body<TokenResponse>()

    public suspend fun getTokenByRefreshToken(refreshToken: String): TokenResponse =
        httpClient.submitForm(
            "/realms/${config.realm}/protocol/openid-connect/token",
            parameters {
                append("refresh_token", refreshToken)
                append("client_id", config.clientId)
                append("grant_type", "refresh_token")
            },
        ).body<TokenResponse>()

    public suspend fun getTokenByClientSecret(clientSecret: String): TokenResponse =
        httpClient.submitForm(
            "/realms/${config.realm}/protocol/openid-connect/token",
            parameters {
                append("client_secret", clientSecret)
                append("client_id", config.clientId)
                append("grant_type", "client_credentials")
            },
        ).body<TokenResponse>()

    public suspend fun createUser(
        user: UserRepresentation,
        accessToken: String,
    ) {
        httpClient.post("/admin/realms/${config.realm}/users") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            setBody(user)
        }
    }

    public suspend fun getUsers(
        user: UserRepresentation? = null,
        exact: Boolean? = null,
        accessToken: String,
    ): Set<UserRepresentation> =
        httpClient.get("/admin/realms/${config.realm}/users") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")

            user?.let {
                (it.attributes as Map<*, *>?)?.let {
                    parameter("q", it.entries.joinToString(" ") { (k, v) -> "$k:${json.encode(v)}" })
                }
                (json.encodeToAny(it) as Map<*, *>).filter { (k, v) -> k !== "attributes" && v != null }.forEach { (k, v) ->
                    parameter(k.toString(), v)
                }
            }

            exact?.let { parameter("exact", exact) }
        }.body<Set<UserRepresentation>>()

    public suspend fun updateUser(
        user: UserRepresentation,
        accessToken: String
    ) {
        httpClient.put("/admin/realms/${config.realm}/users/${user.id}") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            contentType(ContentType.Application.Json)
            setBody(user)
        }
    }

    public suspend fun deleteUser(
        userId: String,
        accessToken: String,
    ) {
        httpClient.delete("/admin/realms/${config.realm}/users/$userId") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }
    }

    public suspend fun getUserInfo(accessToken: String): UserInfo =
        httpClient.get("/realms/${config.realm}/protocol/openid-connect/userinfo") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }.body<UserInfo>()

    public suspend fun getUserRealmRoles(
        userId: String,
        accessToken: String,
    ): Set<RoleRepresentation> =
        httpClient.get("/admin/realms/${config.realm}/users/$userId/role-mappings/realm") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }.body<Set<RoleRepresentation>>()

    public suspend fun resetPassword(
        userId: String,
        resetPassword: ResetPassword,
        accessToken: String
    ) {
        httpClient.put("/admin/realms/${config.realm}/users/$userId/reset-password") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            contentType(ContentType.Application.Json)
            setBody(resetPassword)
        }
    }

    public suspend fun updatePassword(userId: String, accessToken: String) {
        httpClient.post("/admin/realms/${config.realm}/users/$userId/execute-actions-email") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            contentType(ContentType.Application.Json)
            setBody(UpdatePassword(listOf("UPDATE_PASSWORD")))
        }
    }
}
