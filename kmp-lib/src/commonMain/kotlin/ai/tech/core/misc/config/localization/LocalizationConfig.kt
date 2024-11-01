package ai.tech.core.misc.config.localization

import ai.tech.core.misc.location.weblate.model.WeblateConfig
import kotlinx.serialization.Serializable

@Serializable
public data class LocalizationConfig(
    val weblate: Map<String, WeblateConfig> = emptyMap()
)
