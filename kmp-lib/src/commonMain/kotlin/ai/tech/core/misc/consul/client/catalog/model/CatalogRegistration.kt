package ai.tech.core.misc.consul.client.catalog.model

import ai.tech.core.misc.consul.client.model.Check
import ai.tech.core.misc.consul.client.model.Service
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class CatalogRegistration(
    @SerialName("ID")
val id: String? = null,
    @SerialName("Datacenter")
val datacenter: String? = null,
    @SerialName("Node")
val node: String,
    @SerialName("Address")
val address: String,
    @SerialName("NodeMeta")
val nodeMeta: Map<String, String>,
    @SerialName("TaggedAddresses")
val taggedAddresses: TaggedAddresses? = null,
    @SerialName("Service")
val service: Service? = null,
    @SerialName("Check")
val check: Check? = null,
    @SerialName("WriteRequest")
val writeRequest: WriteRequest? = null,
    @SerialName("SkipNodeUpdate")
val skipNodeUpdate: Boolean?
)
