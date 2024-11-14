package ai.tech.core.misc.consul.client.health.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ServiceHealth(
    @SerialName("Service")
    val service: Service,
    @SerialName("Checks")
    val checks: List<HealthCheck>,
    @SerialName("Node")
    val node: Node
)
