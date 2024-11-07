package ai.tech.core.misc.location.localization

import ai.tech.core.misc.location.localization.model.config.Localization
import ai.tech.core.misc.location.model.Language

public open class MapLocalizationService(
    override var language: Language,
    private val localization: Map<String, Localization>
) : AbstractLocalizationService() {

    init {
        languages = localization.keys.map { ai.tech.core.misc.location.model.languages[it]!!() }
    }

    override suspend fun initialize(): Unit = Unit

    override suspend fun localize(language: Language) {
        super.localize(language)

        translations = localization["${language.alpha3}-${language.countryAlpha2}"]!!.translations
    }
}
