package ai.tech.core.misc.auth.firebase.client.admin.model

import ai.tech.core.misc.auth.model.bearer.Token
import kotlinx.serialization.Serializable

@Serializable
public data class SignUpResponse(
    override val idToken: String,
    override val email: String,
    override val refreshToken: String,
    override val expiresIn: String,
    override val localId: String,
) : SignResponse



