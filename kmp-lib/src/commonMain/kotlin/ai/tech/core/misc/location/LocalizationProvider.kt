package ai.tech.core.misc.location

import ai.tech.core.misc.location.model.Language

public interface LocalizationProvider {
    public suspend fun getLanguages(): List<Language>

    public suspend fun getTranslations(language: Language): Map<String, String>
}
