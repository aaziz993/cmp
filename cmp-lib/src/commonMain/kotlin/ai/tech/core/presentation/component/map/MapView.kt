package ai.tech.core.presentation.component.map

import ai.tech.core.misc.location.model.Location
import ai.tech.core.presentation.component.map.model.MapLocation
import ai.tech.core.presentation.component.map.model.MapViewConfig
import ai.tech.core.presentation.component.map.model.MapViewLocalization
import androidx.compose.runtime.Composable

@Composable
public expect fun MapView(
    config: MapViewConfig = MapViewConfig(),
    markers: List<MapLocation>? = null,
    onMarkerClick: ((Location, href: String?) -> Boolean)? = null,
    routes: List<List<Location>>? = null,
    onSelect: ((removed: Set<Location>, added: Set<Location>) -> Unit)? = null,
    localization: MapViewLocalization = MapViewLocalization(),
)
