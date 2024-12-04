package ai.tech.core.misc.consul.client.operator.model

import ai.tech.core.misc.type.serialization.serializer.bignum.BigIntegerSerial
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class RaftConfiguration(
    @SerialName("Servers")
val servers: List<RaftServer>,
    @SerialName("Index")
val index: BigIntegerSerial
)
