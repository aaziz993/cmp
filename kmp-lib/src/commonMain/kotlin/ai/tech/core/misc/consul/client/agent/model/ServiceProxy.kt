package ai.tech.core.misc.consul.client.agent.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ServiceProxy(
    @SerialName("DestinationServiceName")
    val destinationServiceName: String? = null,
    @SerialName("Config")
    val config: List<String> = listOf(),
    @SerialName("DestinationServiceID")
    val destinationServiceId: String? = null,
    @SerialName("LocalServiceAddress")
    val localServiceAddress: String? = null,
    @SerialName("LocalServicePort")
    val localServicePort: Int? = null,
    @SerialName("Upstreams")
    val upstreams: List<ServiceProxyUpstream> = listOf()
)
