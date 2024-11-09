package ai.tech.core.presentation.component.image.avatar.model

import kotlinx.serialization.Serializable

@Serializable
public data class AvatarLocalization(
    val resetPassword: String = "Reset password",
    val signOut: String = "Sign out",
)
