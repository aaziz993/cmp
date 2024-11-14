package ai.tech.core.misc.consul.model.parameter

import kotlinx.serialization.Serializable

@Serializable
public data class PutParameters(
    val cas: Long? = null,
    val acquire: String? = null,
    val release: String? = null,
    val dc: String? = null,
    val token: String? = null,
) : Parameters
