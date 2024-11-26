package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class LookupResponse(
    val kind: String? = null,
    val users: List<UserRecord>? = null
)
