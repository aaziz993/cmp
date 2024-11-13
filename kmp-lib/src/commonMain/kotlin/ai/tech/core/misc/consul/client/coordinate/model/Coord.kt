package ai.tech.core.misc.consul.client.coordinate.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Coord(
    @SerialName("Adjustment")
val adjustment: Double,
    @SerialName("Error")
val error: Double,
    @SerialName("Height")
val height: Double,
    @SerialName("Vec")
val vec: DoubleArray? = null
)
