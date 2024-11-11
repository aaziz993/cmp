package ai.tech.home.map.model.config

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.misc.location.model.config.LocationConfig
import ai.tech.core.misc.model.config.presentation.DestinationConfig
import ai.tech.core.presentation.component.map.model.MapViewConfig
import kotlinx.serialization.Serializable

@Serializable
public data class MapScreenConfig(
    override val route: String,
    override val auth: AuthResource? = null,
    val view: MapViewConfig,
    val location: LocationConfig
) : DestinationConfig
