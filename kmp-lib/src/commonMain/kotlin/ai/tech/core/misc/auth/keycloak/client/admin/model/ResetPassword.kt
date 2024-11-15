package ai.tech.core.misc.auth.keycloak.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class ResetPassword(
    val value: String,
    val type: String = "password",
    val temporary: Boolean = false,
)
