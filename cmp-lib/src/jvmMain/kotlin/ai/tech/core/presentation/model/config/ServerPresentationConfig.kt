package ai.tech.core.presentation.model.config

import ai.tech.core.misc.model.config.presentation.PresentationConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ServerPresentationConfig(
    override val routeBase: String? = null,
    override val route: String,
    override val signInRedirectRoute: String = route,
    override val signOutRedirectRoute: String = route,
) : PresentationConfig
