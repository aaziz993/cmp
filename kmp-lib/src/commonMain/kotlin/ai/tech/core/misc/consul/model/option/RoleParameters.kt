package ai.tech.core.misc.consul.model.option

import kotlinx.serialization.Serializable

@Serializable
public data class RoleParameters(
    val policy: String? = null,
    val ns: String? = null
) : Parameters
