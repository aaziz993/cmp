package ai.tech.core.misc.location.localization

import ai.tech.core.misc.location.model.Language

public open class MapLocalizationService(
    override var languages: List<Language>,
    override var translations: Map<String, List<String>>
) : AbstractLocalizationService()
