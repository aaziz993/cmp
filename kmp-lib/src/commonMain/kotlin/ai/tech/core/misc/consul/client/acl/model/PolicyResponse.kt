package ai.tech.core.misc.consul.client.acl.model

import ai.tech.core.misc.type.serialization.serializer.bignum.BigIntegerSerial
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class PolicyResponse(
    @SerialName("ID")
    override val id: String,
    @SerialName("Name")
    override val name: String,
    @SerialName("Datacenters")
    override val datacenters: String? = null,
    @SerialName("Hash")
    override val hash: String,
    @SerialName("CreateIndex")
    override val createIndex: BigIntegerSerial,
    @SerialName("ModifyIndex")
    override val modifyIndex: BigIntegerSerial,
) : BasePolicyResponse
