package ai.tech.core.misc.consul.client.agent.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Agent(
    @SerialName("Config")
val config: Config? = null,
    @SerialName("DebugConfig")
val debugConfig: DebugConfig? = null,
    @SerialName("Member")
val member: Member? = null
)
