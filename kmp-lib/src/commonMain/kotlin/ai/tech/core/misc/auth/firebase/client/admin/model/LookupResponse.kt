package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class LookupResponse(
    val users: List<UserRecord>? = null
)
