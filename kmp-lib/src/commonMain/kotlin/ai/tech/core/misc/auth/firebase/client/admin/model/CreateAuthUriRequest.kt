package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class CreateAuthUriRequest(
    val identifier: String, // User's email address
    val continueUri: String, // The URI to which the IDP redirects the user back. For this use case, this is just the current URL.
)
