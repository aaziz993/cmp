package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class ResetPasswordResponse(
    val email: String,
    val requestType: String // Should be "PASSWORD_RESET".
)
