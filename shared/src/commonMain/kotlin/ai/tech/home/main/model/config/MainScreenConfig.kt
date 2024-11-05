package ai.tech.home.main.model.config

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.presentation.model.config.DestinationConfig
import kotlinx.serialization.Serializable

@Serializable
public data class MainScreenConfig(
    override val route: String,
    override val auth: AuthResource? = null,
) : DestinationConfig
