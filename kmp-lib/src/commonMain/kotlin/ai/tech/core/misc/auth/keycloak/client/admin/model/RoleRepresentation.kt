package ai.tech.core.misc.auth.keycloak.client.admin.model

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
public data class RoleRepresentation(
    val attributes: Map<String, @Contextual Any>? = null,
    val clientRole: Boolean? = null,
    val composite: Boolean? = null,
    val containerId: String? = null,
    val description: String? = null,
    val id: String? = null,
    val name: String? = null,
)
