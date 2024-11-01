package ai.tech.core.misc.auth.keycloak

import core.auth.keycloak.model.ResetPassword
import core.auth.keycloak.model.RoleRepresentation
import core.auth.keycloak.model.UserInfo
import core.auth.keycloak.model.UserRepresentation
import core.auth.model.oauth.OAuthClientConfig
import core.auth.model.oauth.Token
import core.io.model.http.server.HttpResponseException
import core.type.encode
import core.type.toGeneric
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

public class KeycloakClient(
    httpClient: HttpClient,
    public val config: OAuthClientConfig,
) {

    @OptIn(ExperimentalSerializationApi::class)
    public val httpClient: HttpClient = httpClient.config {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                explicitNulls = false
            })
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    private val json = Json {
        explicitNulls = false
        ignoreUnknownKeys = true
    }

    public suspend fun getToken(username: String, password: String): Result<Token> =
        httpClient.submitForm("${config.address}/realms/${config.realm}/protocol/openid-connect/token", parameters {
            append("username", username)
            append("password", password)
            append("client_id", config.clientId)
            append("grant_type", "password")
        }).let {
            if (it.status == HttpStatusCode.OK) {
                Result.success(it.body<Token>())
            } else {
                Result.failure(HttpResponseException(it.status, it.bodyAsText()))
            }
        }

    public suspend fun getToken(refreshToken: String): Result<Token> =
        httpClient.submitForm("${config.address}/realms/${config.realm}/protocol/openid-connect/token", parameters {
            append("refresh_token", refreshToken)
            append("client_id", config.clientId)
            append("grant_type", "refresh_token")
        }).let {
            if (it.status == HttpStatusCode.OK) {
                Result.success(it.body<Token>())
            } else {
                Result.failure(HttpResponseException(it.status, it.bodyAsText()))
            }
        }

    public suspend fun getToken(): Result<Token> =
        httpClient.submitForm("${config.address}/realms/${config.realm}/protocol/openid-connect/token", parameters {
            append("client_secret", config.clientSecret!!)
            append("client_id", config.clientId)
            append("grant_type", "client_credentials")
        }).let {
            if (it.status == HttpStatusCode.OK) {
                Result.success(it.body<Token>())
            } else {
                Result.failure(HttpResponseException(it.status, it.bodyAsText()))
            }
        }

    public suspend fun createUser(
        user: UserRepresentation,
        accessToken: String? = null,
    ): Result<Unit> =
        httpClient.post("${config.address}/admin/realms/${config.realm}/users") {
            accessToken?.let { header(HttpHeaders.Authorization, "Bearer $it") }
            setBody(user)
        }.let {
            if (it.status == HttpStatusCode.Created) {
                Result.success(Unit)
            } else {
                Result.failure(HttpResponseException(it.status, it.bodyAsText()))
            }
        }

    public suspend fun getUsers(
        user: UserRepresentation? = null,
        exact: Boolean? = null,
        accessToken: String,
    ): Result<Set<UserRepresentation>> =
        httpClient.get("${config.address}/admin/realms/${config.realm}/users") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")

            user?.let { json.toGeneric<UserRepresentation, Map<*, *>>(it) }?.let {
                it.filter { (k, v) -> k !== "attributes" && v != null }.forEach { (k, v) ->
                    parameter(k.toString(), v)
                }

                (it["attributes"] as Map<*, *>?)?.let {
                    parameter(
                        "q",
                        it.entries.joinToString(" ") { (k, v) -> "$k:${json.encode(v)}" })
                }
            }

            if (exact != null) {
                parameter("exact", exact)
            }

        }.let {
            if (it.status == HttpStatusCode.OK) {
                Result.success(it.body<Set<UserRepresentation>>())
            } else {
                Result.failure(HttpResponseException(it.status, it.bodyAsText()))
            }
        }

    public suspend fun updateUser(
        user: UserRepresentation,
        accessToken: String
    ): Result<Unit> =
        httpClient.put("${config.address}/admin/realms/${config.realm}/users/${user.id}") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            contentType(ContentType.Application.Json)
            setBody(user)
        }.let {
            if (it.status == HttpStatusCode.NoContent) {
                Result.success(Unit)
            } else {
                Result.failure(HttpResponseException(it.status, it.bodyAsText()))
            }
        }

    public suspend fun deleteUser(
        userId: String,
        accessToken: String,
    ): Result<Unit> =
        httpClient.delete("${config.address}/admin/realms/${config.realm}/users/$userId") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }.let {
            if (it.status == HttpStatusCode.NoContent) {
                Result.success(Unit)
            } else {
                Result.failure(HttpResponseException(it.status, it.bodyAsText()))
            }
        }

    public suspend fun getUserInfo(accessToken: String): Result<UserInfo> =
        httpClient.get("${config.address}/realms/${config.realm}/protocol/openid-connect/userinfo") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }.let {
            if (it.status == HttpStatusCode.OK) {
                Result.success(it.body<UserInfo>())
            } else {
                Result.failure(HttpResponseException(it.status, it.bodyAsText()))
            }
        }

    public suspend fun getUserRealmRoles(
        userId: String,
        accessToken: String,
    ): Result<Set<RoleRepresentation>> =
        httpClient.get("${config.address}/admin/realms/${config.realm}/users/$userId/role-mappings/realm") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
        }.let {
            if (it.status == HttpStatusCode.OK) {
                Result.success(it.body<Set<RoleRepresentation>>())
            } else {
                Result.failure(HttpResponseException(it.status, it.bodyAsText()))
            }
        }

    public suspend fun resetPassword(
        userId: String,
        resetPassword: ResetPassword,
        accessToken: String
    ): Result<Unit> =
        httpClient.put("${config.address}/admin/realms/${config.realm}/users/$userId/reset-password") {
            header(HttpHeaders.Authorization, "Bearer $accessToken")
            contentType(ContentType.Application.Json)
            setBody(resetPassword)
        }.let {
            if (it.status == HttpStatusCode.NoContent) {
                Result.success(Unit)
            } else {
                Result.failure(HttpResponseException(it.status, it.bodyAsText()))
            }
        }

    public suspend fun forgetPassword(email: String): Result<Unit> {
        TODO()
    }
}
