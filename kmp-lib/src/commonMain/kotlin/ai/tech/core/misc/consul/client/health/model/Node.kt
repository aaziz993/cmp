package ai.tech.core.misc.consul.client.health.model

import ai.tech.core.misc.consul.client.catalog.model.TaggedAddresses
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Node(
    @SerialName("Node")
    val name: String,
    @SerialName("Address")
    val address: String,
    @SerialName("Datacenter")
    val datacenter: String,
    @SerialName("TaggedAddresses")
    val taggedAddresses: TaggedAddresses? = null,
    @SerialName("Meta")
    val meta: Map<String, String>? = null,
)
