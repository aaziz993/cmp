package ai.tech.core.misc.location.localization

import ai.tech.core.misc.location.model.Language
import ai.tech.core.misc.type.multiple.replaceWithArgs
import kotlin.toString

public abstract class AbstractLocalizationService {

    public open lateinit var languages: List<Language>
        protected set

    public open lateinit var language: Language
        protected set

    public open lateinit var translations: Map<String, List<String>>
        protected set

    public abstract suspend fun initialize()

    public open suspend fun localize(language: Language) {
        this.language = language
    }

    public fun translate(key: String, quantity: Int = 0, vararg formatArgs: Any): String = translations[key]?.let {
        it[quantity].replaceWithArgs(formatArgs.map { it.toString() })
    } ?: key
}


