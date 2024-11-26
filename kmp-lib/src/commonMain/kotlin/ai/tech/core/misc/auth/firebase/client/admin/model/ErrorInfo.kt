package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class ErrorInfo(
    val index: Int,
    val message: String,
    val localId: String?,
)
