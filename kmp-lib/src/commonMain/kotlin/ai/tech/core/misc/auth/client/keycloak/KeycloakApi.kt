package ai.tech.core.misc.auth.client.keycloak

import ai.tech.core.misc.auth.client.keycloak.model.ExecuteActionsEmail
import ai.tech.core.misc.auth.client.keycloak.model.ResetPassword
import ai.tech.core.misc.auth.client.keycloak.model.RoleRepresentation
import ai.tech.core.misc.auth.client.keycloak.model.TokenResponse
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
        @Header("Authorization") authorization: String,
        @Path("realm") realm: String,
        @Body userRepresentation: UserRepresentation,

        )

    @GET("/admin/realms/{realm}/users")
    suspend fun getUsers(
        @Header("Authorization") authorization: String,
        @Path("realm") realm: String,
        @QueryMap parameters: Map<String, String>,
    ): Set<UserRepresentation>

    @Headers("Content-Type: application/json")
    @PUT("/admin/realms/{realm}/users/{userId}")
    suspend fun updateUser(
        @Header("Authorization") authorization: String,
        @Path("realm") realm: String,
        @Path("userId") userId: String,
        @Body userRepresentation: UserRepresentation,
    )

    @DELETE("/admin/realms/{realm}/users/{userId}")
    suspend fun deleteUser(
        @Header("Authorization") authorization: String,
        @Path("realm") realm: String,
        @Path("userId") userId: String,
    )

    @GET("/realms/{realm}/protocol/openid-connect/userinfo")
    suspend fun getUserInfo(
        @Header("Authorization") authorization: String,
        @Path("realm") realm: String,
    ): UserInfo

    @GET("/admin/realms/{realm}/users/{userId}/role-mappings/realm")
    suspend fun getUserRealmRoles(
        @Header("Authorization") authorization: String,
        @Path("realm") realm: String,
        @Path("userId") userId: String,
    ): Set<RoleRepresentation>

    @Headers("Content-Type: application/json")
    @PUT("/admin/realms/{realm}/users/{userId}/reset-password")
    suspend fun resetPassword(
        @Header("Authorization") authorization: String,
        @Path("realm") realm: String,
        @Path("userId") userId: String,
        @Body resetPassword: ResetPassword,
    )

    @Headers("Content-Type: application/json")
    @POST("/admin/realms/{realm}/users/{userId}/execute-actions-email")
    suspend fun updatePassword(
        @Header("Authorization") authorization: String,
        @Path("realm") realm: String,
        @Path("userId") userId: String,
        @Body executeActionsEmail: ExecuteActionsEmail,
    )
}
