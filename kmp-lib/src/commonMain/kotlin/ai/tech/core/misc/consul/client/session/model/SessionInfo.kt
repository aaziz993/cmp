package ai.tech.core.misc.consul.client.session.model

import ai.tech.core.misc.consul.client.serializer.ConsulDurationSerializer
import ai.tech.core.misc.type.serializer.bignum.BigIntegerSerial
import kotlin.time.Duration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SessionInfo(
    @SerialName("Node")
    val node: String,
    @SerialName("ID")
    val id: String,
    @SerialName("Name")
    val name: String? = null,
    @SerialName("LockDelay")
    val localDelay: Long,
    @SerialName("Behavior")
    val behavior: String,
    @Serializable(with = ConsulDurationSerializer::class)
    @SerialName("TTL")
    val ttl: Duration? = null,
    @SerialName("Checks")
    val checks: List<String>,
    @SerialName("NodeChecks")
    val nodeChecks: List<String>,
    @SerialName("ServiceChecks")
    val serviceChecks: List<String>? = null,
    @SerialName("CreateIndex")
    val createIndex: BigIntegerSerial,
    @SerialName("ModifyIndex")
    val modifyIndex: BigIntegerSerial
)
