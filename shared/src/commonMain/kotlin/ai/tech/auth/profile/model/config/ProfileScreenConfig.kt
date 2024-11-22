package ai.tech.auth.profile.model.config

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.presentation.model.config.ScreenConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ProfileScreenConfig(
    override val route: String = "profile",
    override val auth: AuthResource,
) : ScreenConfig
