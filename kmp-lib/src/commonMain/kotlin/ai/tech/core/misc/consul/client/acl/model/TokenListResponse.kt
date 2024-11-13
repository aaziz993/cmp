package ai.tech.core.misc.consul.client.acl.model

import ai.tech.core.misc.type.serializer.bignum.BigIntegerSerial
import kotlinx.datetime.LocalDate
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TokenListResponse (
    @SerialName("SecretID")
val secretId: String,
    @SerialName("AccessorID")
    override val accessorId: String,
    @SerialName("Description")
    override val description: String,
    @SerialName("Policies")
    override val policies: List<Token.PolicyLink>,
    @SerialName("CreateIndex")
    override val createIndex: BigIntegerSerial,
    @SerialName("ModifyIndex")
    override val modifyIndex: BigIntegerSerial,
    @SerialName("Local")
    override val local: Boolean,
    @SerialName("CreateTime")
    override val createTime: LocalDate,
    @SerialName("Hash")
    override val hash: String,
) : BaseTokenResponse
