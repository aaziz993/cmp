package ai.tech.core.misc.auth.client.keycloak.model

import kotlinx.serialization.Serializable

@Serializable
public data class UpdatePassword(
    val actions: List<String>,
    val redirectUri: String? = null
)
