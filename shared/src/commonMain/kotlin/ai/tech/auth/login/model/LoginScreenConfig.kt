package ai.tech.auth.login.model

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.misc.model.config.presentation.DestinationConfig
import kotlinx.serialization.Serializable

@Serializable
public data class LoginScreenConfig(
    override val route: String,
) : DestinationConfig {
    override val auth: AuthResource?
        get() = null
}
