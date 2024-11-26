package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class SignInWithIdpRequest(
    val idToken: String,
    val requestUri: String,
    val postBody: String,
    val returnSecureToken: Boolean = true, // Should always be true.
    // Whether to force the return of the OAuth credential on the following errors: FEDERATED_USER_ID_ALREADY_LINKED and EMAIL_EXISTS.
    val returnIdpCredential: Boolean = true
)
