package ai.tech.core.presentation.component.picker.localization

import ai.tech.core.misc.location.model.Language
import ai.tech.core.presentation.component.picker.AdvancedPickerDialog
import ai.tech.core.presentation.component.picker.localization.model.LanguagePickerItem
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState
import ai.tech.core.presentation.component.textfield.search.model.rememberSearchFieldState
import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape

@Composable
public fun AdvancedLanguagePickerDialog(
    modifier: Modifier = Modifier,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    searchFieldState: SearchFieldState? = rememberSearchFieldState(),
    searchPlaceholder: (@Composable () -> Unit)? = null,
    searchSingleLine: Boolean = true,
    items: List<Language>,
    currentItem: Language = items.first(),
    itemLocalization: (Language) -> String = { it.toString() },
    onItemClick: (Language) -> Unit,
    onDismissRequest: () -> Unit,
): Unit =
    AdvancedPickerDialog(
        modifier,
        shape,
        colors,
        elevation,
        border,
        searchFieldState,
        searchPlaceholder,
        searchSingleLine,
        compareMatchers = listOf(0, -3),
        items = items.map { LanguagePickerItem(it, itemLocalization(it)) },
        currentItem = LanguagePickerItem(currentItem, itemLocalization(currentItem)),
        itemText = { itemLocalization(it) },
        onItemClick = { onItemClick(it.value!!) },
        onDismissRequest = onDismissRequest,
    )

