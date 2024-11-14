package ai.tech.core.misc.consul.model.parameter

import kotlinx.serialization.Serializable

@Serializable
public data class DeleteParameters(
    val cas: Long? = null,
    val recurse: Boolean? = null,
    val dc: String? = null,
) : Parameters
