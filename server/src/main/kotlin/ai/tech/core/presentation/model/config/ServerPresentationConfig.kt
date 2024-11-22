package ai.tech.core.presentation.model.config

import ai.tech.core.misc.model.config.presentation.PresentationConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ServerPresentationConfig(
    override val route: String? = null,
    override val startDestination: String,
    override val signInRedirectDestination: String,
    override val signOutRedirectDestination: String,
    override val destination: SharedDestinationConfig
) : PresentationConfig
