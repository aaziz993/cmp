package ai.tech.core.misc.consul.module

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TaggedAddresses(
    @SerialName("wan") val wan: String? = null, @SerialName("lan") val lan: String? = null
)
