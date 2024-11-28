package ai.tech.core.misc.auth.firebase.client.admin.model

import ai.tech.core.misc.auth.model.bearer.BearerToken
import kotlinx.serialization.Serializable

@Serializable
public data class SignInWithCustomTokenResponse(
    override val idToken: String,
    override val refreshToken: String,
    val expiresIn: String,
) : BearerToken