package ai.tech.core.misc.auth.client.keycloak.model

import kotlinx.serialization.Serializable

@Serializable
public data class UserRepresentation(
    val access: Access? = null,
    val attributes: Map<String, List<String>>? = null,
    val clientConsents: Set<UserConsentRepresentation>? = null,
    val clientRoles: Set<RoleRepresentation>? = null,
    val createdTimestamp: Long? = null,
    val credentials: Set<CredentialRepresentation>? = null,
    val disableableCredentialTypes: Set<String>? = null,
    val email: String? = null,
    val emailVerified: Boolean? = null,
    val enabled: Boolean? = null,
    val federatedIdentities: Set<FederatedIdentityRepresentation>? = null,
    val federationLink: String? = null,
    val firstName: String? = null,
    val groups: Set<String>? = null,
    val id: String? = null,
    val lastName: String? = null,
    val notBefore: Long? = null,
    val origin: String? = null,
    val realmRoles: Set<String>? = null,
    val requiredActions: Set<String>? = null,
    val self: String? = null,
    val serviceAccountClientId: String? = null,
    val username: String? = null,
    val totp: Boolean? = null,
)
