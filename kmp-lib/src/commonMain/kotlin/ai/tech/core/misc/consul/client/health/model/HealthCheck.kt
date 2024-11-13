package ai.tech.core.misc.consul.client.health.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class HealthCheck(
    @SerialName("CheckID")
    val checkId: String? = null,
    @SerialName("Status")
    val status: String? = null,
    @SerialName("Notes")
    val notes: List<String> = listOf(),
    @SerialName("Output")
    val output: List<String> = listOf(),
    @SerialName("ServiceID")
    val serviceId: List<String> = listOf(),
    @SerialName("ServiceName")
    val serviceName: List<String> = listOf(),
    @SerialName("ServiceTags")
    val serviceTags: List<String> = listOf(),
    @SerialName("Name")
    val name: String? = null,
    @SerialName("Node")
    val node: String? = null
)
