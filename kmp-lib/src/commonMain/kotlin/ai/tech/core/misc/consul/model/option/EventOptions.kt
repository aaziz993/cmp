package ai.tech.core.misc.consul.model.option

import kotlinx.serialization.Serializable

@Serializable
public data class EventOptions(
    val dc: String? = null,
    val node: String? = null,
    val service: String? = null,
    val tag: String? = null,
) : ParamAdder
