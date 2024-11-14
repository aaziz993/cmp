package ai.tech.core.misc.consul.model.option

import kotlinx.serialization.Serializable

@Serializable
public data class DeleteParameters(
    val cas: Long? = null,
    val recurse: Boolean? = null,
    val dc: String? = null,
) : Parameters
