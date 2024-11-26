package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.json.JsonObject

@Serializable
public data class UpdateResponse(
    val localId: String,
    val email: String? = null,
    val phoneNumber: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    val emailVerified: Boolean? = null,
    val providerUserInfo: List<UserInfo>? = null,
    val passwordHash: String? = null,
    val passwordUpdatedAt: Long? = null,
    val validSince: String? = null,
    @SerialName("deleteProvider")
    private val _deleteProvider: List<String>? = null,
    val customClaims: JsonObject? = null,
    val emailLinkSignin: Boolean? = null
) {

    @Transient
    var deleteAttribute: List<String> = listOfNotNull(
        if (displayName == null) {
            "DISPLAY_NAME"
        }
        else {
            null
        },
        if (photoUrl == null) {
            "PHOTO_URL"
        }
        else {
            null
        },
    )

    @Transient
    val deleteProvider: List<String> = _deleteProvider?.let { it + "phone" } ?: listOf("phone")
}
