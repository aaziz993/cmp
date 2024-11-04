package ai.tech.core.presentation.component.map.model

import ai.tech.core.misc.location.model.LocationImpl
import kotlinx.serialization.Serializable

@Serializable
public data class MapViewConfig(
    val initialZoom: Int? = null,
    val initialCenter: LocationImpl? = null,
    val zoomable: Boolean = true,
    val movable: Boolean = true,
    val tilePicker: Boolean = true,
    val googleApiKey: String? = null,
)
