package ai.tech.core.misc.consul.client.kv.model

import ai.tech.core.misc.type.serializer.bignum.BigIntegerSerial
import ai.tech.core.misc.type.serializer.primitive.Base64Serializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Operation(
    @SerialName("Verb")
    val verb: String,
    @SerialName("Key")
    val key: String? = null,
    @SerialName("Value")
    @Serializable(with = Base64Serializer::class)
    val value: String? = null,
    @SerialName("Flags")
    val flags: Long? = null,
    @SerialName("Index")
    val index: BigIntegerSerial? = null,
    @SerialName("Session")
    val session: String? = null
)
