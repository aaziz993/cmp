package ai.tech.core.misc.location.localization.weblate.client.model

import kotlinx.serialization.Serializable

@Serializable
public data class WeblateConfig(
    val address: String,
    val apiKey: String,
)