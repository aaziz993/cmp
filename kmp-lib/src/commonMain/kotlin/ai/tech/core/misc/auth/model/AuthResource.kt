package ai.tech.core.misc.auth.model

import ai.tech.core.misc.type.multiple.iterable.model.ContainResolution
import kotlinx.serialization.Serializable

@Serializable
public data class AuthResource(
    val providers: List<String?> = listOf(null),
    val roles: Set<String>? = null,
    val roleResolution: ContainResolution = ContainResolution.ANY,
)
