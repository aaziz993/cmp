package ai.tech.core.presentation.model.config

import kotlinx.serialization.Serializable

@Serializable
public data class ServerPresentationConfig(
    override val route: String? = null,
    override val startDestination: String,
    override val signInRedirectDestination: String = startDestination,
    override val signOutRedirectDestination: String = startDestination,
    override val destination: DestinationConfig
) : SharedPresentationConfig
