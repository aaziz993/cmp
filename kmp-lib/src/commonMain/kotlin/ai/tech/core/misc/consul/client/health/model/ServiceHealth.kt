package ai.tech.core.misc.consul.client.health.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ServiceHealth(
    @SerialName("Service")
    val service: Service? = null,
    @SerialName("Checks")
    val checks: List<HealthCheck> = listOf(),
    @SerialName("Node")
    val node: Node? = null
)
