package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TokenRequest(
    @SerialName("grant_type")
    val grantType: String = "refresh_token", // should always be refresh_token
    @SerialName("refresh_token")
    val refreshToken: String
)
