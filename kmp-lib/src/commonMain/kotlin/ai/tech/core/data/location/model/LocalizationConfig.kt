package ai.tech.core.data.location.model

import ai.tech.core.data.location.weblate.model.WeblateConfig
import kotlinx.serialization.Serializable

@Serializable
public data class LocalizationConfig(
    val weblate: Map<String, WeblateConfig> = emptyMap()
)