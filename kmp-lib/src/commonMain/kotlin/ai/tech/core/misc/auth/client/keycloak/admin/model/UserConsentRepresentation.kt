package ai.tech.core.misc.auth.client.keycloak.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class UserConsentRepresentation(
    val clientId: String,
    val createdDate: String? = null,
    val grantedClientScopes: List<String>? = null,
    val lastUpdatedDate: String? = null,
)
