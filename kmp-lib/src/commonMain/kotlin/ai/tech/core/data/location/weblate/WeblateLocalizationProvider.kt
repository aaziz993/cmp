package ai.tech.core.data.location.weblate

import ai.tech.core.data.location.LocalizationProvider
import ai.tech.core.data.location.model.Language
import ai.tech.core.data.location.weblate.model.WeblateTranslationsResponse
import ai.tech.core.data.location.weblate.model.WeblateUnitsResponse

public class WeblateLocalizationProvider(
    public val client: WeblateClient,
    public val projectName: String,
) : LocalizationProvider {
    override suspend fun getLanguages(): List<Language> =
       ai.tech. core.data.location.model.languages.entries.partition { it.key.contains("-") }
            .let { (regionLanguageCodes, languageCodes) ->
                mutableListOf<Language>().apply {
                    var translations = client.getTranslations().getOrThrow()

                    while (true) {
                        addAll(translations.results.filter { projectName == it.component.project.name }
                            .mapNotNull {
                                (it.language.aliases + it.language.code).partition { it.contains("_") }
                                    .let { (weblateRegionLanguageCodes, weblateLanguageCodes) ->
                                        (weblateRegionLanguageCodes.map { it.split("_", limit = 2) }
                                            .sortedByDescending { it[0].length }
                                            .firstNotNullOfOrNull { (weblateLanguage, weblateRegion) ->
                                                regionLanguageCodes.find {
                                                    it.key.split("-")
                                                        .let { it[0].startsWith(weblateLanguage) && it[1] == weblateRegion }
                                                }
                                            } ?: weblateLanguageCodes.sortedByDescending { it.length }
                                            .firstNotNullOfOrNull { weblateLanguageCode ->
                                                languageCodes.find { it.key == weblateLanguageCode }
                                            })?.value?.invoke()
                                    }
                            })

                        translations.nextPath?.let {
                            translations = client.request<WeblateTranslationsResponse>(it)
                                .getOrThrow()
                        } ?: break
                    }
                }
            }

    override suspend fun getTranslations(language: Language): Map<String, String> =
        mutableMapOf<String, String>().apply {

            var units = client.getUnits().getOrThrow()

            while (true) {
                putAll(units.results.mapNotNull {
                    val match = weblateUnitTranslationRegex.matchEntire(it.translation)
                    if (match != null && match.groupValues[1] == projectName && match.groupValues[3] == language.alpha2) {
                        "${match.groupValues[2]}_${it.context}" to it.target.first()
                    } else {
                        null
                    }
                })

                units.nextPath?.let {
                    units = client.request<WeblateUnitsResponse>(it).getOrThrow()
                } ?: break
            }
        }

    public companion object {
        private val weblateUnitTranslationRegex = "^.*?/translations/(.*?)/(.*?)/(.*?)/.*$".toRegex()
    }
}