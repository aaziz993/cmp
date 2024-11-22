package ai.tech.auth.forgotpassword.model.config

import ai.tech.core.misc.auth.model.AuthResource
import ai.tech.core.presentation.model.config.ScreenConfig
import kotlinx.serialization.Serializable

@Serializable
public data class ForgotPasswordScreenConfig(
    override val route: String,
) : ScreenConfig {
    override val auth: AuthResource?
        get() = null
}
