package ai.tech.auth.forgotpassword.model.config

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.presentation.model.config.DestinationConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ForgotPasswordScreenConfig(
    override val route: String,
) : DestinationConfig {
    override val auth: AuthResource?
        get() = null
}
