package ai.tech.core.misc.consul.client.health.model

import ai.tech.core.misc.consul.client.catalog.model.ServiceWeights
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Service(
    @SerialName("Address")
    val address: String,
    @SerialName("ID")
    val id: String,
    @SerialName("Port")
    val port: Int,
    @SerialName("Service")
    val service: String? = null,
    @SerialName("EnableTagOverride")
    val enableTagOverride: Boolean? = null,
    @SerialName("Tags")
    val tags: List<String>,
    @SerialName("Meta")
    val meta: Map<String, String>,
    @SerialName("Weights")
    val weights: ServiceWeights? = null
)
