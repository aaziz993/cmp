package ai.tech.core.misc.auth.client.keycloak.admin

import ai.tech.core.misc.auth.client.keycloak.admin.model.ExecuteActionsEmail
import ai.tech.core.misc.auth.client.keycloak.admin.model.ResetPassword
import ai.tech.core.misc.auth.client.keycloak.admin.model.RoleRepresentation
import ai.tech.core.misc.auth.client.keycloak.admin.model.UserInfo
import ai.tech.core.misc.auth.client.keycloak.admin.model.UserRepresentation
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.POST
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface KeycloakAdminApi {

    @Headers("Content-Type: application/json")
    @POST("/admin/realms/{realm}/users")
    suspend fun createUser(
        @Path("realm") realm: String,
        @Body userRepresentation: UserRepresentation,
    )

    @GET("/admin/realms/{realm}/users")
    suspend fun getUsers(
        @Path("realm") realm: String,
        @QueryMap parameters: Map<String, String>,
    ): Set<UserRepresentation>

    @Headers("Content-Type: application/json")
    @PUT("/admin/realms/{realm}/users/{userId}")
    suspend fun updateUser(
        @Path("realm") realm: String,
        @Path("userId") userId: String,
        @Body userRepresentation: UserRepresentation,
    )

    @DELETE("/admin/realms/{realm}/users/{userId}")
    suspend fun deleteUser(
        @Path("realm") realm: String,
        @Path("userId") userId: String,
    )

    @GET("/realms/{realm}/protocol/openid-connect/userinfo")
    suspend fun getUserInfo(
        @Path("realm") realm: String,
    ): UserInfo

    @GET("/admin/realms/{realm}/users/{userId}/role-mappings/realm")
    suspend fun getUserRealmRoles(
        @Path("realm") realm: String,
        @Path("userId") userId: String,
    ): Set<RoleRepresentation>

    @Headers("Content-Type: application/json")
    @PUT("/admin/realms/{realm}/users/{userId}/reset-password")
    suspend fun resetPassword(
        @Path("realm") realm: String,
        @Path("userId") userId: String,
        @Body resetPassword: ResetPassword,
    )

    @Headers("Content-Type: application/json")
    @POST("/admin/realms/{realm}/users/{userId}/execute-actions-email")
    suspend fun updatePassword(
        @Path("realm") realm: String,
        @Path("userId") userId: String,
        @Body executeActionsEmail: ExecuteActionsEmail,
    )
}
