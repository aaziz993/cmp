package ai.tech.core.misc.location.localization

import ai.tech.core.misc.location.model.Language
import org.jetbrains.compose.resources.StringResource

public class ResLocalizationProvider(
    override var languages: List<Language>,
    private val translations: Map<String, StringResource>
) : AbstractLocalizationService() {

    override fun translate(key: String): String = translations[key]?.key ?: key
}
