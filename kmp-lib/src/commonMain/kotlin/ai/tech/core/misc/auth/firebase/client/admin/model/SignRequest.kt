package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

// You can sign in a user anonymously by issuing an HTTP POST request to the Auth signupNewUser endpoint with email and password equal to null.
@Serializable
public data class SignRequest(
    val email: String? = null,
    val password: String? = null,
    val returnSecureToken: Boolean = true
)
