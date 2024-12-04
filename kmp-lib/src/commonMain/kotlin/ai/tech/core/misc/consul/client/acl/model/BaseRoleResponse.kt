package ai.tech.core.misc.consul.client.acl.model

import ai.tech.core.misc.type.serialization.serializer.bignum.BigIntegerSerial

public interface BaseRoleResponse {

    public val id: String
    public val name: String
    public val description: String
    public val policies: List<Role.RolePolicyLink>
    public val serviceIdentities: List<Role.RoleServiceIdentity>
    public val nodeIdentities: List<Role.RoleNodeIdentity>
    public val createIndex: BigIntegerSerial
    public val modifyIndex: BigIntegerSerial
    public val hash: String
}
