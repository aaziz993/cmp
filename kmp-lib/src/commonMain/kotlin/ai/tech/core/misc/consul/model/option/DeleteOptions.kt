package ai.tech.core.misc.consul.model.option

import kotlinx.serialization.Serializable

@Serializable
public data class DeleteOptions(
    val cas: Long? = null,
    val recurse: Boolean? = null,
    val dc: String? = null,
) : ParamAdder
