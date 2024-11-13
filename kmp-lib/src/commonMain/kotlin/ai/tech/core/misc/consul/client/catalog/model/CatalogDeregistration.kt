package ai.tech.core.misc.consul.client.catalog.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class CatalogDeregistration(
    @SerialName("Datacenter")
val datacenter: String? = null,
    @SerialName("Node")
val node: String,
    @SerialName("CheckID")
val checkId: String? = null,
    @SerialName("ServiceID")
val serviceId: String? = null,
    @SerialName("WriteRequest")
val writeRequest: WriteRequest? = null,
)
