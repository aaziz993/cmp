package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class BatchDeleteRequest(
    val localIds: List<String>,
    val force: Boolean = true
)
