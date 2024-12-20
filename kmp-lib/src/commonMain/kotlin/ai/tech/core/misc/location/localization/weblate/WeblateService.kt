package ai.tech.core.misc.location.localization.weblate

import ai.tech.core.misc.location.localization.AbstractLocalizationService
import ai.tech.core.misc.location.localization.weblate.client.WeblateClient
import ai.tech.core.misc.location.model.Language
import ai.tech.core.misc.type.multiple.toList
import io.ktor.http.Url

public class WeblateService(
    public val client: WeblateClient,
    public val project: String,
    override var language: Language,
) : AbstractLocalizationService() {

    override suspend fun initialize() {
        translations = client.units.toList().flatMap {
            it.results.mapNotNull {
                val segments = Url(it.translation).segments
                if (segments[2] == project) {
                    "${segments[3]}_${it.context}" to listOf(it.target.first())
                }
                else {
                    null
                }
            }
        }.toMap()
    }

    override suspend fun localize(language: Language) {
        super.localize(language)

        val (languageWithRegionCodes, languageCodes) = ai.tech.core.misc.location.model.languages.entries.partition { it.key.contains("-") }

        languages = client.translations.toList().flatMap {
            it.results.filter { project == it.component.project.name }.mapNotNull {
                val (weblateLanguageWithCodes, weblateLanguageCodes) = (it.language.aliases + it.language.code).partition { it.contains("_") }

                (weblateLanguageWithCodes.map { it.split("_", limit = 2) }
                    .sortedByDescending { it[0].length }
                    .firstNotNullOfOrNull { (weblateLanguage, weblateRegion) ->
                        languageWithRegionCodes.find {
                            it.key.split("-")
                                .let { it[0].startsWith(weblateLanguage) && it[1] == weblateRegion }
                        }
                    } ?: weblateLanguageCodes.sortedByDescending { it.length }
                    .firstNotNullOfOrNull { weblateLanguageCode ->
                        languageCodes.find { it.key == weblateLanguageCode }
                    })?.value?.invoke()
            }
        }
    }
}
