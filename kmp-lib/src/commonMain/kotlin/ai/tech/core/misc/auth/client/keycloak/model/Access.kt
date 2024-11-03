package ai.tech.core.misc.auth.client.keycloak.model

import kotlinx.serialization.Serializable

@Serializable
public data class Access(
    val manageGroupMembership: Boolean,
    val view: Boolean,
    val mapRoles: Boolean,
    val impersonate: Boolean,
    val manage: Boolean
)
