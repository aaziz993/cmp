package profile.model

import core.auth.model.AuthResource
import core.presentation.model.config.DestinationConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ProfileScreenConfig(
    override val route: String,
    override val auth: AuthResource,
) : DestinationConfig