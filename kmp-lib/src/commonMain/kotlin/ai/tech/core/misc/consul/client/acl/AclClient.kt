package ai.tech.core.misc.consul.client.acl

import ai.tech.core.misc.consul.client.acl.model.AclResponse
import ai.tech.core.misc.consul.client.acl.model.AclToken
import ai.tech.core.misc.consul.client.acl.model.Policy
import ai.tech.core.misc.consul.client.acl.model.PolicyResponse
import ai.tech.core.misc.consul.client.acl.model.Role
import ai.tech.core.misc.consul.client.acl.model.RoleListResponse
import ai.tech.core.misc.consul.client.acl.model.RoleResponse
import ai.tech.core.misc.consul.client.acl.model.Token
import ai.tech.core.misc.consul.client.acl.model.TokenListResponse
import ai.tech.core.misc.consul.client.acl.model.TokenResponse
import ai.tech.core.misc.consul.model.option.RoleOptions
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.engine.ProxyBuilder.http

public class AclClient(ktorfit: Ktorfit, config: ClientConfig, eventCallback: ClientEventCallback) : BaseClient(CLIENT_NAME, config, eventCallback) {

    private val api: AclApi = ktorfit.createAclApi()

    @Deprecated("Use a different method")
    public suspend fun create(aclToken: AclToken): String {
        return http.extract(api.create(aclToken)).id
    }

    @Deprecated("Use a different method")
    public suspend fun update(aclToken: AclToken) {
        http.handle(api.update(aclToken))
    }

    @Deprecated("Use a different method")
    public suspend fun destroy(id: String) {
        http.handle(api.destroy(id))
    }

    @Deprecated("Use a different method")
    public suspend fun getInfo(id: String): List<AclResponse> {
        return http.extract(api.getInfo(id))
    }

    @Deprecated("Use a different method")
    public suspend fun cloneAcl(id: String): String {
        return http.extract(api.clone(id)).id
    }

    @Deprecated("Use a different method")
    public suspend fun listAcls(): List<AclResponse> {
        return http.extract(api.list())
    }

    public suspend fun createPolicy(policy: Policy): PolicyResponse {
        return http.extract(api.createPolicy(policy))
    }

    public suspend fun readPolicy(id: String): PolicyResponse {
        return http.extract(api.readPolicy(id))
    }

    public suspend fun readPolicyByName(name: String): PolicyResponse {
        return http.extract(api.readPolicyByName(name))
    }

    public suspend fun updatePolicy(id: String, policy: Policy): PolicyResponse {
        return http.extract(api.updatePolicy(id, policy))
    }

    public suspend fun deletePolicy(id: String) {
        http.extract(api.deletePolicy(id))
    }

    public suspend fun listPolicies(): List<PolicyResponse> {
        return http.extract(api.listPolicies())
    }

    public suspend fun createToken(token: Token): TokenResponse {
        return http.extract(api.createToken(token))
    }

    public suspend fun cloneToken(id: String, token: Token): TokenResponse {
        return http.extract(api.cloneToken(id, token))
    }

    public suspend fun readToken(id: String): TokenResponse {
        return http.extract(api.readToken(id))
    }

    public suspend fun readSelfToken(): TokenResponse {
        return http.extract(api.readToken("self"))
    }

    public suspend fun updateToken(id: String, token: Token): TokenResponse {
        return http.extract(api.updateToken(id, token))
    }

    public suspend fun listTokens(): List<TokenListResponse> {
        return listTokens(TokenQueryOptions.BLANK)
    }

    public suspend fun listTokens(queryOptions: TokenQueryOptions): List<TokenListResponse> {
        return http.extract(api.listTokens(queryOptions.toQuery()))
    }

    public suspend fun deleteToken(id: String) {
        http.extract(api.deleteToken(id))
    }

    public suspend fun createRole(token: Role): RoleResponse {
        return http.extract(api.createRole(token))
    }

    public suspend fun readRole(id: String): RoleResponse {
        return http.extract(api.readRole(id))
    }

    public suspend fun readRoleByName(name: String): RoleResponse {
        return http.extract(api.readRoleByName(name))
    }

    public suspend fun updateRole(id: String, role: Role): RoleResponse {
        return http.extract(api.updateRole(id, role))
    }

    public suspend fun listRoles(): List<RoleListResponse> {
        return listRoles(RoleOptions.BLANK)
    }

    public suspend fun listRoles(roleOptions: RoleOptions): List<RoleListResponse> {
        return http.extract(api.listRoles(roleOptions.toQuery()))
    }

    public suspend fun deleteRole(id: String) {
        http.extract(api.deleteRole(id))
    }

    public companion object {
        private const val CLIENT_NAME = "acl"
    }
}

