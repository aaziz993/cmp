package ai.tech.core.misc.consul.module

import ai.tech.core.misc.type.serializer.bignum.BigIntegerSerial
import com.ionspin.kotlin.bignum.integer.BigInteger
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class KVMetadata(
    @SerialName("CreateIndex") val createIndex: BigIntegerSerial,
    @SerialName("ModifyIndex") val modifyIndex: BigIntegerSerial,
    @SerialName("LockIndex") val lockIndex: BigIntegerSerial,
    @SerialName("Session") val session: String? = null,
    @SerialName("Key") val key: String,
    @SerialName("Flags") val flags: Int,
    @SerialName("Value") val value: String,
    @Transient
//    @HttpHeader("X-Consul-Index")
    var xConsulIndex: String? = null
) {

    @OptIn(ExperimentalEncodingApi::class)
    @Suppress("unused")
    public fun decoded(): ByteArray= Base64.Default.decode(value)

    public fun consulIndex(): BigInteger {
        return BigInteger.Companion.parseString(xConsulIndex!!)
    }
}
