package ai.tech.core.presentation.component.picker.localization

import ai.tech.core.misc.location.model.Language
import ai.tech.core.misc.location.model.languages
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf

public val LocalLocalization: ProvidableCompositionLocal<Language> = staticCompositionLocalOf { languages["eng-US"]!!() }

@Composable
public fun Localization(language: Language, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        LocalLocalization provides language,
        content = content
    )
}
