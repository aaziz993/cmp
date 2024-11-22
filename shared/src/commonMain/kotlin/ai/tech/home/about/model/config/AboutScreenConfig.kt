package ai.tech.home.about.model.config

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.misc.model.config.presentation.ScreenConfig
import kotlinx.serialization.Serializable

@Serializable
public data class AboutScreenConfig(
    override val route: String,
    override val auth: AuthResource? = null,
) : ScreenConfig
