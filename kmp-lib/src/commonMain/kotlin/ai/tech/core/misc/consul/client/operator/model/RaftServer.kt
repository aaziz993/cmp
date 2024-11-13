package ai.tech.core.misc.consul.client.operator.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class RaftServer(
    @SerialName("ID")
val id: String,
    @SerialName("Node")
val node: String,
    @SerialName("Address")
val address: String,
    @SerialName("Leader")
val leader: Boolean,
    @SerialName("Voter")
val voter: Boolean
)
