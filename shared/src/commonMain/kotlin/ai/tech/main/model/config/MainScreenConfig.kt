package ai.tech.main.model.config

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.presentation.model.config.ScreenConfig
import kotlinx.serialization.Serializable

@Serializable
public data class MainScreenConfig(
    override val route: String = "main",
    override val auth: AuthResource? = null,
) : ScreenConfig
