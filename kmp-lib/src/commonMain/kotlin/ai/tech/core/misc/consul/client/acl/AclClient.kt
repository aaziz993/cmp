package ai.tech.core.misc.consul.client.acl

import ai.tech.core.misc.consul.client.acl.model.Policy
import ai.tech.core.misc.consul.client.acl.model.PolicyResponse
import ai.tech.core.misc.consul.client.acl.model.Role
import ai.tech.core.misc.consul.client.acl.model.RoleListResponse
import ai.tech.core.misc.consul.client.acl.model.RoleResponse
import ai.tech.core.misc.consul.client.acl.model.Token
import ai.tech.core.misc.consul.client.acl.model.TokenListResponse
import ai.tech.core.misc.consul.client.acl.model.TokenResponse
import ai.tech.core.misc.consul.model.parameter.RoleParameters
import ai.tech.core.misc.consul.model.parameter.TokenQueryParameters
import de.jensklingenberg.ktorfit.Ktorfit

public class AclClient internal constructor(ktorfit: Ktorfit) {
    /**
     * Constructs an instance of this class.
     *
     * @param ktorfit The [Ktorfit] to build a client from.
     */
    private val api: AclApi = ktorfit.createAclApi()

    public suspend fun createPolicy(policy: Policy): PolicyResponse =
        api.createPolicy(policy)

    public suspend fun readPolicy(id: String): PolicyResponse =
        api.readPolicy(id)

    public suspend fun readPolicyByName(name: String): PolicyResponse =
        api.readPolicyByName(name)

    public suspend fun updatePolicy(id: String, policy: Policy): PolicyResponse =
        api.updatePolicy(id, policy)

    public suspend fun deletePolicy(id: String): Unit =
        api.deletePolicy(id)

    public suspend fun listPolicies(): List<PolicyResponse> =
        api.listPolicies()

    public suspend fun createToken(token: Token): TokenResponse =
        api.createToken(token)

    public suspend fun cloneToken(id: String, token: Token): TokenResponse =
        api.cloneToken(id, token)

    public suspend fun readToken(id: String): TokenResponse =
        api.readToken(id)

    public suspend fun readSelfToken(): TokenResponse =
        api.readToken("self")

    public suspend fun updateToken(id: String, token: Token): TokenResponse =
        api.updateToken(id, token)

    public suspend fun listTokens(queryParameters: TokenQueryParameters = TokenQueryParameters()): List<TokenListResponse> =
        api.listTokens(queryParameters.query)

    public suspend fun deleteToken(id: String): Unit =
        api.deleteToken(id)

    public suspend fun createRole(token: Role): RoleResponse =
        api.createRole(token)

    public suspend fun readRole(id: String): RoleResponse =
        api.readRole(id)

    public suspend fun readRoleByName(name: String): RoleResponse =
        api.readRoleByName(name)

    public suspend fun updateRole(id: String, role: Role): RoleResponse =
        api.updateRole(id, role)

    public suspend fun listRoles(roleParameters: RoleParameters = RoleParameters()): List<RoleListResponse> =
        api.listRoles(roleParameters.query)

    public suspend fun deleteRole(id: String): Unit =
        api.deleteRole(id)
}

