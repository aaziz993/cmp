package ai.tech.core.misc.consul.client.acl.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class AclToken public constructor(
    @SerialName("ID")
val id: String? = null,
    @SerialName("Name")
val name: String? = null,
    @SerialName("Type")
val type: String? = null,
    @SerialName("Rules")
val rules: String? = null,
)
