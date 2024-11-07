@file:Suppress("INVISIBLE_REFERENCE", "INVISIBLE_MEMBER")
@file:OptIn(ExperimentalResourceApi::class, InternalResourceApi::class)

package ai.tech.core.misc.location.localization

import ai.tech.core.misc.location.model.Language
import androidx.compose.ui.unit.Density
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.InternalResourceApi
import org.jetbrains.compose.resources.LanguageQualifier
import org.jetbrains.compose.resources.RegionQualifier
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.StringArrayResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.ThemeQualifier
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.getStringArray

public class ResLocalizationService(
    override var languages: List<Language>,
    private val stringResources: Map<String, StringResource> = emptyMap(),
    private val stringArrayResources: Map<String, StringArrayResource> = emptyMap(),
) : AbstractLocalizationService() {

    override suspend fun initialize(): Unit = Unit

    override suspend fun localize(language: Language) {
        super.localize(language)

        val resourceEnvironment = ResourceEnvironment(
            LanguageQualifier(language.alpha3),
            RegionQualifier(language.countryAlpha2.orEmpty()),
            ThemeQualifier.LIGHT,
            Density(0f),
        )

        translations = stringResources.mapValues { (_, v) ->
            listOf(getString(resourceEnvironment, v))
        } + stringArrayResources.mapValues { (_, v) ->
            getStringArray(resourceEnvironment, v)
        }
    }
}
