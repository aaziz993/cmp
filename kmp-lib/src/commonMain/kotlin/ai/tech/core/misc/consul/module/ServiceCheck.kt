package ai.tech.core.misc.consul.module

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ServiceCheck(
    @SerialName("Service") val service: Service? = null,
    @SerialName("Checks") val checks: List<HealthCheck> = listOf(),
    @SerialName("Node") val node: Node? = null
)