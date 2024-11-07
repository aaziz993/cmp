package ai.tech.core.misc.location.model.config

import ai.tech.core.misc.location.localization.weblate.model.WeblateConfig
import kotlinx.serialization.Serializable

@Serializable
public data class LocalizationConfig(
    val provider: String,
    // "alpha3-countryAlpha2"
    val language: String = "eng-US",
    val weblate: Map<String, WeblateConfig> = emptyMap())
