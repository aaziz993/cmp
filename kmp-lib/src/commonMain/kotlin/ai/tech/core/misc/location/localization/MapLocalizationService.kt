package ai.tech.core.misc.location.localization

import ai.tech.core.misc.location.model.Language

public class MapLocalizationService(
    override var languages: List<Language>,
    private val translations: Map<String, String>
) : AbstractLocalizationService() {

    override fun translate(key: String): String = translations[key] ?: key
}
