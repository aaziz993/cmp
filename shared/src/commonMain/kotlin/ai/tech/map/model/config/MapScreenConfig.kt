package ai.tech.map.model.config

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.misc.location.model.config.LocationConfig
import ai.tech.core.presentation.model.config.ScreenConfig
import ai.tech.core.presentation.component.map.model.MapViewConfig
import kotlinx.serialization.Serializable

@Serializable
public data class MapScreenConfig(
    override val route: String = "map",
    override val auth: AuthResource? = null,
    val view: MapViewConfig = MapViewConfig(),
    val location: LocationConfig
) : ScreenConfig
