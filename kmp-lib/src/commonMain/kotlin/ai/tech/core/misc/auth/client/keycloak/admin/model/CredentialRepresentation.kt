package ai.tech.core.misc.auth.client.keycloak.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class CredentialRepresentation(
    val createdDate: Long? = null,
    val credentialData: String? = null,
    val id: String? = null,
    val priority: Int? = null,
    val secretData: String? = null,
    val temporary: Boolean? = null,
    val type: String? = null,
    val userLabel: String? = null,
    val value: String? = null,
)
