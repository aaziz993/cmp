package ai.tech.home.map.model.config

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.presentation.component.map.model.MapViewConfig
import ai.tech.core.misc.model.config.presentation.DestinationConfig
import kotlinx.serialization.Serializable
import map.location.model.LocationConfig

@Serializable
public data class MapScreenConfig(
    override val route: String,
    override val auth: AuthResource? = null,
    val view: MapViewConfig,
    val location: LocationConfig
) : DestinationConfig
