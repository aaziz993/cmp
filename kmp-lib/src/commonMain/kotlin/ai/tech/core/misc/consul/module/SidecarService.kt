package ai.tech.core.misc.consul.module

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SidecarService(
    @SerialName("Tags") val tags: List<String>? = null,
    @SerialName("Port") val port: String? = null,
    @SerialName("Proxy") val proxy: ServiceProxy? = null,
)
