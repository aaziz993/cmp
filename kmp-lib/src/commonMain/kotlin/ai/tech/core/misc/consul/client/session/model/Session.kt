package ai.tech.core.misc.consul.client.session.model

import ai.tech.core.misc.consul.client.serializer.ConsulDurationSerializer
import kotlin.time.Duration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Session(
    @SerialName("LockDelay")
    val lockDelay: String? = null,
    @SerialName("Name")
    val name: String? = null,
    @SerialName("Node")
    val node: String? = null,
    @SerialName("Checks")
    val checks: List<String>,
    @SerialName("Behavior")
    val behavior: Behavior? = null,
    @Serializable(with = ConsulDurationSerializer::class)
    @SerialName("TTL")
    val ttl: Duration? = null
)
