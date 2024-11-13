package ai.tech.core.misc.consul.client.acl.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AclTokenId(
    @SerialName("ID")
val id: String
)
