package ai.tech.core.misc.consul.client.kv.model

import ai.tech.core.misc.type.serialization.serializer.bignum.BigIntegerSerial
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TxError(
    @SerialName("OpIndex")
val opIndex: BigIntegerSerial? = null,
    @SerialName("What")
val what: String? = null
)
