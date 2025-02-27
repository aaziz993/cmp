package ai.tech.core.misc.consul.client.catalog.model

import ai.tech.core.misc.consul.client.model.Service
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class CatalogNode(
    @SerialName("Services")
    val services: Map<String, Service>? = null,
    @SerialName("Node")
    val node: Node? = null
)
