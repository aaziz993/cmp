package ai.tech.core.presentation.component.map

import ai.tech.core.misc.location.model.Location
import ai.tech.core.presentation.component.map.model.MapLocation
import ai.tech.core.presentation.component.map.model.MapViewConfig
import ai.tech.core.presentation.component.map.model.MapViewLocalization
import androidx.compose.runtime.Composable

@Composable
public actual fun MapView(
    config: MapViewConfig,
    markers: List<MapLocation>?,
    onMarkerClick: ((Location, href: String?) -> Boolean)?,
    routes: List<List<Location>>?,
    onSelect: ((removed: Set<Location>, added: Set<Location>) -> Unit)?,
    localization: MapViewLocalization,
) {
}
