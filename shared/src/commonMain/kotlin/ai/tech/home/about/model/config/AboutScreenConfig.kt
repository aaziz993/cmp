package ai.tech.home.about.model.config

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.presentation.model.config.ScreenConfig
import kotlinx.serialization.Serializable

@Serializable
public data class AboutScreenConfig(
    override val route: String,
    override val auth: AuthResource? = null,
) : ScreenConfig
