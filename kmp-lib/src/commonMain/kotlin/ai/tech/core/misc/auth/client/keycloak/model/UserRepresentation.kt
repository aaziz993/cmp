package ai.tech.core.misc.auth.client.keycloak.model

import ai.tech.core.misc.auth.model.User
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
){
    private fun UserRepresentation.toUser(): User = User(
        username,
        firstName,
        lastName,
        attributes?.get(USER_PHONE_ATTRIBUTE_KEY)?.get(0),
        email,
        attributes?.get(USER_IMAGE_ATTRIBUTE_KEY)?.get(0),
        realmRoles,
        attributes?.toMutableMap()?.apply {
            remove(USER_PHONE_ATTRIBUTE_KEY)
            remove(USER_IMAGE_ATTRIBUTE_KEY)
        },
    )

    private fun User.toUserRepresentation(): UserRepresentation =
        UserRepresentation(
            username = username,
            firstName = firstName,
            lastName = lastName,
            email = email,
            realmRoles = roles,
            attributes = if (!(phone == null && image == null)) {
                (attributes ?: emptyMap()) + mutableMapOf<String, List<String>>().apply {
                    phone?.let { this[USER_PHONE_ATTRIBUTE_KEY] = listOf(it) }
                    image?.let { this[USER_IMAGE_ATTRIBUTE_KEY] = listOf(it) }
                }
            }
            else {
                attributes
            },
        )

    public companion object {

        public const val USER_PHONE_ATTRIBUTE_KEY: String = "phone"
        public const val USER_IMAGE_ATTRIBUTE_KEY: String = "image"
    }
}
