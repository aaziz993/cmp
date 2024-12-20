package ai.tech.core.misc.auth.keycloak.client.token.model

import ai.tech.core.misc.auth.client.bearer.model.BearerToken
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TokenResponse(
    @SerialName("access_token")
    override val token: String,
    @SerialName("expires_in")
    val expiresIn: Int,
    @SerialName("refresh_expires_in")
    val refreshExpiresIn: Int?,
    @SerialName("refresh_token")
    override val refreshToken: String,
    @SerialName("token_type")
    val tokenType: String,
    @SerialName("not-before-policy")
    val notBeforePolicy: Int?,
    @SerialName("session_state")
    val sessionState: String?,
    @SerialName("scope")
    val scope: String,
    @SerialName("id_token")
    val idToken: String? = null
) : BearerToken
