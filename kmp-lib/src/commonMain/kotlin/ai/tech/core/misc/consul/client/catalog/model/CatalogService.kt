package ai.tech.core.misc.consul.client.catalog.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class CatalogService(
    @SerialName("Datacenter")
val datacenter: String? = null,
    @SerialName("ServiceName")
val serviceName: String? = null,
    @SerialName("ServiceID")
val serviceId: String? = null,
    @SerialName("ServiceAddress")
val serviceAddress: String? = null,
    @SerialName("ServiceEnableTagOverride")
val serviceEnableTagOverride: Boolean = false,
    @SerialName("ServicePort")
val servicePort: Int? = null,
    @SerialName("ServiceTags")
val serviceTags: List<String> = listOf(),
    @SerialName("ServiceMeta")
val serviceMeta: List<String> = listOf(),
    @SerialName("ServiceWeights")
val serviceWeights: ServiceWeights? = null,
    @SerialName("NodeMeta")
val nodeMeta: List<String> = listOf(),
    @SerialName("Address")
val address: String? = null,
    @SerialName("Node")
val node: String? = null
)
