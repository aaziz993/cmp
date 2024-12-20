package ai.tech.core.misc.location.localization.weblate.client.model

import kotlinx.serialization.Serializable

@Serializable
public data class WeblateComponent(
    val name: String,
    val slug: String,
    val project: WeblateProject,
)
