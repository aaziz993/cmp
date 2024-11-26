package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class SignInWithIdpResponse(
    val federatedId: String,
    val providerId: String,
    val localId: String,
    val emailVerified: Boolean,
    val email: String,
    val oauthIdToken: String? = null,
    val oauthAccessToken: String? = null,
    val oauthTokenSecret: String? = null,
    val rawUserInfo: String? = null,
    val firstName: String? = null,
    val lastName: String? = null,
    val fullName: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val idToken: String,
    val refreshToken: String,
    val expiresIn: String
)
