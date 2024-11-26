package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
public data class UpdateRequest(
    val localId: String? = null,
    val oobCode: String? = null,
    val email: String? = null,
    val passwordHash: String? = null,
    val providerUserInfo: UserInfo? = null,
    val idToken: String? = null,
    val refreshToken: String? = null,
    val expiresIn: String? = null,
    val phoneNumber: String? = null,
    val emailVerified: Boolean? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val disabled: Boolean? = null,
    val password: String? = null,
    val customClaims: JsonObject? = null,
    val deleteProvider: List<String>? = null,
    val validSince: Long? = null,
    val returnSecureToken: String? = null,
)
