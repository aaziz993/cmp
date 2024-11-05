package ai.tech.home.settings.model.config

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.misc.model.config.presentation.DestinationConfig
import kotlinx.serialization.Serializable

@Serializable
public data class SettingsScreenConfig(
    override val route: String,
    override val auth: AuthResource? = null,
) : DestinationConfig
