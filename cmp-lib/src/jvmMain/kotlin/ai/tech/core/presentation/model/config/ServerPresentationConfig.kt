package ai.tech.core.presentation.model.config

import ai.tech.core.misc.model.config.presentation.PresentationConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ServerPresentationConfig(
    override val routeBase: String? = null,
    override val startDestination: String,
    override val signInRedirectDestination: String = startDestination,
    override val signOutRedirectDestination: String = startDestination,
) : PresentationConfig
