package ai.tech.core.misc.consul.model.parameter

import kotlinx.serialization.Serializable

@Serializable
public data class EventParameters(
    val dc: String? = null,
    val node: String? = null,
    val service: String? = null,
    val tag: String? = null,
) : Parameters
