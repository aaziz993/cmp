package ai.tech.core.misc.consul.module

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Service(
    @SerialName("Address") val address: String? = null,
    @SerialName("ID") val id: String? = null,
    @SerialName("Port") val port: Int? = null,
    @SerialName("Service") val service: String? = null,
    @SerialName("EnableTagOverride") val enableTagOverride: Boolean = false,
    @SerialName("Tags") val tags: List<String> = listOf(),
    @SerialName("Meta") val meta: List<String> = listOf(),
    @SerialName("Weights") val weights: ServiceWeights? = null
)
