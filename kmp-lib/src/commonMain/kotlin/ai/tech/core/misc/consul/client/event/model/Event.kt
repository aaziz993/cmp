package ai.tech.core.misc.consul.client.event.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Event(
    @SerialName("ID")
val id: String,
    @SerialName("Name")
val name: String,
    @SerialName("Payload")
val payload: String? = null,
    @SerialName("NodeFilter")
val nodeFilter: String? = null,
    @SerialName("ServiceFilter")
val serviceFilter: String? = null,
    @SerialName("TagFilter")
val tagFilter: String? = null,
    @SerialName("Version")
val version: Int,
    @SerialName("LTime")
val lTime: Long
)
