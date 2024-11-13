package ai.tech.core.misc.consul.client.acl.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Policy(
    @SerialName("ID")
val id: String? = null,
    @SerialName("Description")
val description: String? = null,
    @SerialName("Name")
val name: String,
    @SerialName("Rules")
val rules: String? = null,
    @SerialName("Datacenters")
val datacenters: List<String>? = null,
)
