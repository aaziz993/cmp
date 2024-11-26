package ai.tech.core.misc.auth.firebase.client.admin.model

import kotlinx.serialization.Serializable

@Serializable
public data class ListResponse(
    val users: List<UserRecord>,
    val nextPageToken: String
)
