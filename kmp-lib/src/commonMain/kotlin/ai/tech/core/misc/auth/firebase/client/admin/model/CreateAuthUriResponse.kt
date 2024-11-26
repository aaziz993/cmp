package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class CreateAuthUriResponse(
    val allProviders: List<String>,
    val registered: Boolean,
)
