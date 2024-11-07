package ai.tech.core.misc.location.localization

import ai.tech.core.misc.location.model.Language

public abstract class AbstractLocalizationService {

    public open lateinit var languages: List<Language>
        protected set

    public lateinit var language: Language
        private set

    public open suspend fun localize(language: Language) {
        this.language = language
    }

    public abstract fun translate(key: String): String
}
