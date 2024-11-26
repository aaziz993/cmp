package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class SignInResponse(
    override val idToken: String,
    override val email: String,
    override val refreshToken: String,
    override val expiresIn: String,
    override val localId: String,
    val registered: Boolean,
) : SignResponse
