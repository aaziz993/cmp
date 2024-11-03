package ai.tech.core.misc.auth.client.keycloak.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class UserInfo(
    val sub: String,
    @SerialName("email_verified")
    val emailVerified: Boolean,
    val name: String,
    @SerialName("preferred_username")
    val preferredUsername: String,
    @SerialName("given_name")
    val givenName: String,
    @SerialName("family_name")
    val familyName: String,
    val email: String,
)
