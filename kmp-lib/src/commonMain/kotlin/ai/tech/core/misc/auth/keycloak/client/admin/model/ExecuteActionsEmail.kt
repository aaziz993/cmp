package ai.tech.core.misc.auth.keycloak.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class ExecuteActionsEmail(
    val actions: List<String>,
    val redirectUri: String? = null
)
