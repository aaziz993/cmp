package ai.tech.core.misc.consul.client.agent.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Config(
    @SerialName("Datacenter")
    val datacenter: String? = null,
    @SerialName("NodeName")
    val nodeName: String? = null,
    @SerialName("Revision")
    val revision: String? = null,
    @SerialName("Server")
    val server: Boolean = false,
    @SerialName("Version")
    val version: String? = null
)
