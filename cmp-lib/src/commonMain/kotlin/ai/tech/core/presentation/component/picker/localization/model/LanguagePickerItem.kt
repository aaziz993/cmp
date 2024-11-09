package ai.tech.core.presentation.component.picker.localization.model

import ai.tech.core.misc.location.localization.getCountryFlagDrawerResource
import ai.tech.core.misc.location.model.Language
import ai.tech.core.presentation.component.picker.model.PickerItem
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource

public class LanguagePickerItem(
    language: Language,
    label: String = language.toString(),
) : PickerItem<Language>(
    { Text(modifier = Modifier.padding(horizontal = 18.dp), text = label) },
    {
        Image(
            painter = painterResource(language.getCountryFlagDrawerResource()),
            contentDescription = null,
        )
    },
    null,
    language,
)
