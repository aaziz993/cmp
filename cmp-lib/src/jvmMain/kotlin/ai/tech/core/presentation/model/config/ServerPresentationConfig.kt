package ai.tech.core.presentation.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class ServerPresentationConfig(
    override val routeBase: String? = null,
    override val startDestination: String,
    override val signInRedirectDestination: String = startDestination,
    override val signOutRedirectDestination: String = startDestination,
) : SharedPresentationConfig
