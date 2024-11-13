package ai.tech.core.misc.consul.client.session.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class SessionCreatedResponse(
    @SerialName("ID")
    val id: String
)
