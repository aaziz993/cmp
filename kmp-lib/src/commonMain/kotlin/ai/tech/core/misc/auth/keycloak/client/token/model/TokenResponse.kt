package ai.tech.core.misc.auth.keycloak.client.token.model

import ai.tech.core.misc.auth.model.bearer.Token
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TokenResponse(
    @SerialName("access_token")
    override val accessToken: String,
    @SerialName("expires_in")
    override val expiresIn: Int,
    @SerialName("refresh_expires_in")
    override val refreshExpiresIn: Int?,
    @SerialName("refresh_token")
    override val refreshToken: String,
    @SerialName("token_type")
    override val tokenType: String,
    @SerialName("not-before-policy")
    val notBeforePolicy: Int?,
    @SerialName("session_state")
    val sessionState: String?,
    @SerialName("scope")
    override val scope: String,
    @SerialName("id_token")
    override val idToken: String? = null
) : Token
