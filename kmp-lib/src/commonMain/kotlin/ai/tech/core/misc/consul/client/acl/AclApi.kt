package ai.tech.core.misc.consul.client.acl

import ai.tech.core.misc.consul.client.acl.model.Policy
import ai.tech.core.misc.consul.client.acl.model.PolicyResponse
import ai.tech.core.misc.consul.client.acl.model.Role
import ai.tech.core.misc.consul.client.acl.model.RoleListResponse
import ai.tech.core.misc.consul.client.acl.model.RoleResponse
import ai.tech.core.misc.consul.client.acl.model.Token
import ai.tech.core.misc.consul.client.acl.model.TokenListResponse
import ai.tech.core.misc.consul.client.acl.model.TokenResponse
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface AclApi {

    @PUT("acl/policy")
    suspend fun createPolicy(@Body policy: Policy): PolicyResponse

    @GET("acl/policy/{id}")
    suspend fun readPolicy(@Path("id") id: String): PolicyResponse

    @GET("acl/policy/name/{name}")
    suspend fun readPolicyByName(@Path("name") name: String): PolicyResponse

    @PUT("acl/policy/{id}")
    suspend fun updatePolicy(@Path("id") id: String, @Body policy: Policy): PolicyResponse

    @DELETE("acl/policy/{id}")
    suspend fun deletePolicy(@Path("id") id: String)

    @GET("acl/policies")
    suspend fun listPolicies(): List<PolicyResponse>

    @PUT("acl/token")
    suspend fun createToken(@Body token: Token): TokenResponse

    @PUT("acl/token/{id}/clone")
    suspend fun cloneToken(@Path("id") id: String, @Body token: Token): TokenResponse

    @GET("acl/token/{id}")
    suspend fun readToken(@Path("id") id: String): TokenResponse

    @PUT("acl/token/{id}")
    suspend fun updateToken(@Path("id") id: String, @Body token: Token): TokenResponse

    @GET("acl/tokens")
    suspend fun listTokens(@QueryMap query: Map<String, String> = emptyMap()): List<TokenListResponse>

    @DELETE("acl/token/{id}")
    suspend fun deleteToken(@Path("id") id: String)

    @PUT("acl/role")
    suspend fun createRole(@Body role: Role): RoleResponse

    @GET("acl/role/{id}")
    suspend fun readRole(@Path("id") id: String): RoleResponse

    @GET("acl/role/name/{name}")
    suspend fun readRoleByName(@Path("name") name: String): RoleResponse

    @PUT("acl/role/{id}")
    suspend fun updateRole(@Path("id") id: String, @Body role: Role): RoleResponse

    @DELETE("acl/role/{id}")
    suspend fun deleteRole(@Path("id") id: String)

    @GET("acl/roles")
    suspend fun listRoles(@QueryMap query: Map<String, String> = emptyMap()): List<RoleListResponse>
}

