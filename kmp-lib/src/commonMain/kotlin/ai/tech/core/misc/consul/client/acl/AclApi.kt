package ai.tech.core.misc.consul.client.acl

import ai.tech.core.misc.consul.client.acl.model.AclResponse
import ai.tech.core.misc.consul.client.acl.model.AclToken
import ai.tech.core.misc.consul.client.acl.model.AclTokenId
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

public interface AclApi {

    @Deprecated("Use a different method")
    @PUT("acl/create")
    public suspend fun create(@Body aclToken: AclToken): AclTokenId

    @Deprecated("Use a different method")
    @PUT("acl/update")
    public suspend fun update(@Body aclToken: AclToken)

    @Deprecated("Use a different method")
    @PUT("acl/destroy/{id}")
    public suspend fun destroy(@Path("id") id: String)

    @Deprecated("Use a different method")
    @GET("acl/info/{id}")
    public suspend fun getInfo(@Path("id") id: String): List<AclResponse>

    @Deprecated("Use a different method")
    @PUT("acl/clone/{id}")
    public suspend fun clone(@Path("id") id: String): AclTokenId

    @Deprecated("Use a different method")
    @GET("acl/list")
    public suspend fun list(): List<AclResponse>

    @PUT("acl/policy")
    public suspend fun createPolicy(@Body policy: Policy): PolicyResponse

    @GET("acl/policy/{id}")
    public suspend fun readPolicy(@Path("id") id: String): PolicyResponse

    @GET("acl/policy/name/{name}")
    public suspend fun readPolicyByName(@Path("name") name: String): PolicyResponse

    @PUT("acl/policy/{id}")
    public suspend fun updatePolicy(@Path("id") id: String, @Body policy: Policy): PolicyResponse

    @DELETE("acl/policy/{id}")
    public suspend fun deletePolicy(@Path("id") id: String)

    @GET("acl/policies")
    public suspend fun listPolicies(): List<PolicyResponse>

    @PUT("acl/token")
    public suspend fun createToken(@Body token: Token): TokenResponse

    @PUT("acl/token/{id}/clone")
    public suspend fun cloneToken(@Path("id") id: String, @Body token: Token): TokenResponse

    @GET("acl/token/{id}")
    public suspend fun readToken(@Path("id") id: String): TokenResponse

    @PUT("acl/token/{id}")
    public suspend fun updateToken(@Path("id") id: String, @Body token: Token): TokenResponse

    @GET("acl/tokens")
    public suspend fun listTokens(@QueryMap query: Map<String, String> = emptyMap()): List<TokenListResponse>

    @DELETE("acl/token/{id}")
    public suspend fun deleteToken(@Path("id") id: String)

    @PUT("acl/role")
    public suspend fun createRole(@Body role: Role): RoleResponse

    @GET("acl/role/{id}")
    public suspend fun readRole(@Path("id") id: String): RoleResponse

    @GET("acl/role/name/{name}")
    public suspend fun readRoleByName(@Path("name") name: String): RoleResponse

    @PUT("acl/role/{id}")
    public suspend fun updateRole(@Path("id") id: String, @Body role: Role): RoleResponse

    @DELETE("acl/role/{id}")
    public suspend fun deleteRole(@Path("id") id: String)

    @GET("acl/roles")
    public suspend fun listRoles(@QueryMap query: Map<String, String> = emptyMap()): List<RoleListResponse>
}

