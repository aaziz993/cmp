package ai.tech.core.misc.auth.model.bearer;

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TokenImpl(
    @SerialName("access_token")
    override val accessToken: String,
    @SerialName("expires_in")
    override val expiresIn: Int,
    @SerialName("refresh_token")
    override val refreshToken: String? = null,
    @SerialName("refresh_expires_in")
    override val refreshExpiresIn: Int? = null,
    override val scope: String,
    @SerialName("token_type")
    override val tokenType: String,
    @SerialName("id_token")
    override val idToken: String,
) : Token
