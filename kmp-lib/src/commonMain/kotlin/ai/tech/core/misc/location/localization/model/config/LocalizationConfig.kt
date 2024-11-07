package ai.tech.core.misc.location.localization.model.config

import ai.tech.core.misc.location.localization.weblate.model.WeblateConfig
import ai.tech.core.misc.location.model.Language
import ai.tech.core.misc.location.model.languages
import kotlinx.serialization.Serializable

@Serializable
public data class LocalizationConfig(
    // "alpha3-countryAlpha2"
    val language: String = "eng-US",
    val map: Map<String, Localization> = emptyMap(),
    val weblate: WeblateConfig? = null
) {

    public val foundLanguage: Language = languages[language]!!()
}
