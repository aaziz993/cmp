package ai.tech.core.misc.consul.client.acl.model

import ai.tech.core.misc.type.serialization.serializer.bignum.BigIntegerSerial
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class RoleResponse(
    @SerialName("ID")
    override val id: String,
    @SerialName("Name")
    override val name: String,
    @SerialName("Description")
    override val description: String,
    @SerialName("Policies")
    override val policies: List<Role.RolePolicyLink>,
    @SerialName("ServiceIdentities")
    override val serviceIdentities: List<Role.RoleServiceIdentity>,
    @SerialName("NodeIdentities")
    override val nodeIdentities: List<Role.RoleNodeIdentity>,
    @SerialName("CreateIndex")
    override val createIndex: BigIntegerSerial,
    @SerialName("ModifyIndex")
    override val modifyIndex: BigIntegerSerial,
    @SerialName("Hash")
    override val hash: String
) : BaseRoleResponse
