package ai.tech.core.misc.consul.client.coordinate.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Coordinate(
    @SerialName("Node")
val node: String,
    @SerialName("Coord")
val coord: Coord
)
