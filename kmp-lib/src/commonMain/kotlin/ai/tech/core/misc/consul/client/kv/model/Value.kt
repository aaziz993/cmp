package ai.tech.core.misc.consul.client.kv.model

import ai.tech.core.misc.type.multiple.decode
import ai.tech.core.misc.type.multiple.decodeBase64
import ai.tech.core.misc.type.serialization.serializer.bignum.BigIntegerSerial
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class Value(
    @SerialName("CreateIndex")
    val createIndex: BigIntegerSerial,
    @SerialName("ModifyIndex")
    val modifyIndex: BigIntegerSerial,
    @SerialName("LockIndex")
    val lockIndex: BigIntegerSerial,
    @SerialName("Key")
    val key: String,
    @SerialName("Flags")
    val flags: Long,
    @SerialName("Value")
    val value: String? = null,
    @SerialName("Session")
    val session: String? = null,
) {

    @Transient
    public val decodedValue: String? = value?.decodeBase64()?.decode()
}

