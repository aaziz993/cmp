package ai.tech.core.misc.consul.module

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ServiceWeights(
    @SerialName("Passing") val passing: Int? = null,
    @SerialName("Warning") val warning: Int? = null
)


