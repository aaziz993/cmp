package ai.tech.core.misc.consul.client.agent.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Connect(
    @SerialName("Native")
    val native: Boolean? = null,
    @SerialName("Proxy")
    val proxy: ServiceProxy? = null,
    @SerialName("SidecarService")
    val sidecarService: SidecarService? = null
)
