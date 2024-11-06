package ai.tech.core.misc.consul.module

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ServiceProxyUpstream(
    @SerialName("DestinationType") val destinationType: String? = null,
    @SerialName("DestinationName") val destinationName: String? = null,
    @SerialName("LocalBindPort") val localBindPort: Int? = null
)
