package ai.tech.core.misc.consul.client.coordinate.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Datacenter(
    @SerialName("Datacenter")
val datacenter: String,
    @SerialName("Coordinates")
val coordinates: List<Coordinate>
)
