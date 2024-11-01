package ai.tech.core.misc.location

import ai.tech.core.misc.location.model.Language
import ai.tech.core.misc.location.model.languages

public class TestLocalizationProvider: LocalizationProvider {
    override suspend fun getLanguages(): List<Language> = listOf(
        languages["eng-US"]!!(),
        languages["rus"]!!()
    )

    override suspend fun getTranslations(language: Language): Map<String, String> = emptyMap()
}
