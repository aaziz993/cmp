package ai.tech.core.misc.consul.client.catalog.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class WriteRequest (
    @SerialName("Token")
val token: String
)
