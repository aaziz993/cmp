package ai.tech.core.misc.location.localization.weblate.model

import kotlinx.serialization.Serializable

@Serializable
public data class WeblateUnit(
    val translation: String,
    val context: String,
    val note: String,
    val target: Set<String>
)
