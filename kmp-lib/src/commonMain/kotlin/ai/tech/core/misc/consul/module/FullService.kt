package ai.tech.core.misc.consul.module

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class FullService(
    @SerialName("Address") val address: String? = null,
    @SerialName("ID") val id: String? = null,
    @SerialName("Port") val port: Int? = null,
    @SerialName("Kind") val kind: String? = null,
    @SerialName("Service") val service: String? = null,
    @SerialName("Tags") val tags: List<String> = listOf(),
    @SerialName("Meta") val meta: List<String> = listOf(),
    @SerialName("Weights") val weights: ServiceWeights? = null,
    @SerialName("EnableTagOverride") val enableTagOverride: Boolean = false,
    @SerialName("ContentHash") val contentHash: String? = null,
    @SerialName("Proxy") val proxy: ServiceProxy? = null
)
