package ai.tech.core.misc.consul.client.acl.model

import ai.tech.core.misc.type.serializer.bignum.BigIntegerSerial
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AclResponse(
    @SerialName("CreateIndex")
val createIndex: BigIntegerSerial,
    @SerialName("ModifyIndex")
val modifyIndex: BigIntegerSerial,
    @SerialName("ID")
val id: String? = null,
    @SerialName("Name")
val name: String? = null,
    @SerialName("Type")
val type: String? = null,
    @SerialName("Rules")
val rules: String? = null,
)
