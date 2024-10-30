package ai.tech.core.data.location

import ai.tech.core.data.location.model.Language

public interface LocalizationProvider {
    public suspend fun getLanguages(): List<Language>

    public suspend fun getTranslations(language: Language): Map<String, String>
}