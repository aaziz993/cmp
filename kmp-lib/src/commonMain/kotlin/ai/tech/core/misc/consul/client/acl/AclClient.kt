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
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface AclClient {

    @Deprecated("Use a different method")
    @PUT("acl/create")
    fun create(@Body aclToken: AclToken): Call<AclTokenId>

    @Deprecated("Use a different method")
    @PUT("acl/update")
    fun update(@Body aclToken: AclToken): Call<Unit>

    @Deprecated("Use a different method")
    @PUT("acl/destroy/{id}")
    fun destroy(@Path("id") id: String): Call<Unit>

    @Deprecated("Use a different method")
    @GET("acl/info/{id}")
    fun getInfo(@Path("id") id: String): Call<List<AclResponse>>

    @Deprecated("Use a different method")
    @PUT("acl/clone/{id}")
    fun clone(@Path("id") id: String): Call<AclTokenId>

    @Deprecated("Use a different method")
    @GET("acl/list")
    fun list(): Call<List<AclResponse>>

    @PUT("acl/policy")
    fun createPolicy(@Body policy: Policy): Call<PolicyResponse>

    @GET("acl/policy/{id}")
    fun readPolicy(@Path("id") id: String): Call<PolicyResponse>

    @GET("acl/policy/name/{name}")
    fun readPolicyByName(@Path("name") name: String): Call<PolicyResponse>

    @PUT("acl/policy/{id}")
    fun updatePolicy(@Path("id") id: String, @Body policy: Policy): Call<PolicyResponse>

    @DELETE("acl/policy/{id}")
    fun deletePolicy(@Path("id") id: String): Call<Unit>

    @GET("acl/policies")
    fun listPolicies(): Call<List<PolicyResponse>>

    @PUT("acl/token")
    fun createToken(@Body token: Token): Call<TokenResponse>

    @PUT("acl/token/{id}/clone")
    fun cloneToken(@Path("id") id: String, @Body token: Token): Call<TokenResponse>

    @GET("acl/token/{id}")
    fun readToken(@Path("id") id: String): Call<TokenResponse>

    @PUT("acl/token/{id}")
    fun updateToken(@Path("id") id: String, @Body token: Token): Call<TokenResponse>

    @GET("acl/tokens")
    fun listTokens(@QueryMap query: Map<String, String>): Call<List<TokenListResponse>>

    @DELETE("acl/token/{id}")
    fun deleteToken(@Path("id") id: String): Call<Unit>

    @PUT("acl/role")
    fun createRole(@Body role: Role): Call<RoleResponse>

    @GET("acl/role/{id}")
    fun readRole(@Path("id") id: String): Call<RoleResponse>

    @GET("acl/role/name/{name}")
    fun readRoleByName(@Path("name") name: String): Call<RoleResponse>

    @PUT("acl/role/{id}")
    fun updateRole(@Path("id") id: String, @Body role: Role): Call<RoleResponse>

    @DELETE("acl/role/{id}")
    fun deleteRole(@Path("id") id: String): Call<Unit>

    @GET("acl/roles")
    fun listRoles(@QueryMap query: Map<String, String>): Call<List<RoleListResponse>>
}

