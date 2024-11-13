package ai.tech.core.misc.auth.client.keycloak

import ai.tech.core.misc.auth.client.keycloak.model.ResetPassword
import ai.tech.core.misc.auth.client.keycloak.model.RoleRepresentation
import ai.tech.core.misc.auth.client.keycloak.model.TokenResponse
import ai.tech.core.misc.auth.client.keycloak.model.ExecuteActionsEmail
import ai.tech.core.misc.auth.client.keycloak.model.UserInfo
import ai.tech.core.misc.auth.client.keycloak.model.UserRepresentation
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.Field
import de.jensklingenberg.ktorfit.http.FormUrlEncoded
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Header
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface KeycloakApi {

    @POST("/realms/{realm}/protocol/openid-connect/token")
    @FormUrlEncoded
    suspend fun getToken(
        @Path("realm") realm: String,
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("client_id") clientId: String,
        @Field("grantType") grantType: String = "password",
    ): TokenResponse

    @POST("/realms/{realm}/protocol/openid-connect/token")
    @FormUrlEncoded
    suspend fun getTokenByRefreshToken(
        @Path("realm") realm: String,
        @Field("refresh_token") refreshToken: String,
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String = "refresh_token",
    ): TokenResponse

    @POST("/realms/{realm}/protocol/openid-connect/token")
    @FormUrlEncoded
    suspend fun getTokenByClientSecret(
        @Path("realm") realm: String,
        @Field("client_secret") clientSecret: String,
        @Field("client_id") clientId: String,
        @Field("grant_type") grantType: String = "client_credentials",
    ): TokenResponse

    @Headers("Content-Type: application/json")
    @POST("/admin/realms/{realm}/users")
    suspend fun createUser(
        @Path("realm") realm: String,
        @Body userRepresentation: UserRepresentation,
        @Header("Authorization") accessToken: String,
    )

    @GET("/admin/realms/{realm}/users")
    suspend fun getUsers(
        @Path("realm") realm: String,
        @QueryMap parameters: Map<String, String>,
        @Header("Authorization") accessToken: String,
    ): Set<UserRepresentation>

    @Headers("Content-Type: application/json")
    @PUT("/admin/realms/{realm}/users/{userId}")
    suspend fun updateUser(
        @Path("realm") realm: String,
        @Path("userId") userId: String,
        @Body userRepresentation: UserRepresentation,
        @Header("Authorization") accessToken: String
    )

    @DELETE("/admin/realms/{realm}/users/{userId}")
    suspend fun deleteUser(
        @Path("realm") realm: String,
        @Path("userId") userId: String,
        @Header("Authorization") accessToken: String,
    )

    @GET("/realms/{realm}/protocol/openid-connect/userinfo")
    suspend fun getUserInfo(
        @Path("realm") realm: String,
        @Header("Authorization") accessToken: String
    ): UserInfo

    @GET("/admin/realms/{realm}/users/{userId}/role-mappings/realm")
    suspend fun getUserRealmRoles(
        @Path("realm") realm: String,
        @Path("userId") userId: String,
        @Header("Authorization") accessToken: String,
    ): Set<RoleRepresentation>

    @Headers("Content-Type: application/json")
    @PUT("/admin/realms/{realm}/users/{userId}/reset-password")
    suspend fun resetPassword(
        @Path("realm") realm: String,
        @Path("userId") userId: String,
        @Body resetPassword: ResetPassword,
        @Header("Authorization") accessToken: String
    )

    @Headers("Content-Type: application/json")
    @POST("/admin/realms/{realm}/users/{userId}/execute-actions-email")
    suspend fun updatePassword(
        @Path("realm") realm: String,
        @Path("userId") userId: String,
        @Body executeActionsEmail: ExecuteActionsEmail,
        @Header("Authorization") accessToken: String
    )
}
