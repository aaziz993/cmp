package ai.tech.core.misc.location.weblate.model

import kotlinx.serialization.Serializable

@Serializable
public data class WeblateProject(
    val name: String,
    val slug: String,
)
