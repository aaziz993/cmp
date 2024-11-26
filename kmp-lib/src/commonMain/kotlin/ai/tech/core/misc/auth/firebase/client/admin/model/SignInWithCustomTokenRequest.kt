package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
    public data class SignInWithCustomTokenRequest(
        val token: String,
        val returnSecureToken: Boolean = true, // should always be true
    )
