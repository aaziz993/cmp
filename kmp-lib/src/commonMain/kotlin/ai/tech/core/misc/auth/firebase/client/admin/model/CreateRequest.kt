package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
    public data class CreateRequest(
        val localId: String,
        val email: String,
        val emailVerified: Boolean = true,
        val phoneNumber: String? = null,
        // Password string that is at least 6 characters long.
        val password: String,
        val displayName: String? = null,
        val photoUrl: String? = null,
        val disabled: Boolean = false,
    )
