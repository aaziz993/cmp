package ai.tech.core.data.location.weblate.model

import kotlinx.serialization.Serializable

@Serializable
public data class WeblateLanguage(
    val code: String,
    val name: String,
    val direction: String,
    val aliases: Set<String>
)