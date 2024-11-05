package ai.tech.core.presentation.model.config

import ai.tech.core.misc.model.config.presentation.PresentationConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ServerPresentationConfig(
    override val routeBase: String?,
    override val route: String,
    override val signInRedirectRoute: String,
    override val signOutRedirectRoute: String,
    override val destination: SharedDestinationsConfig
) : PresentationConfig
