package ai.tech.core.presentation.component.picker

import ai.tech.core.presentation.component.picker.model.PickerItem
import ai.tech.core.presentation.component.textfield.search.AdvancedSearchField
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState
import ai.tech.core.presentation.component.textfield.search.model.rememberSearchFieldState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
public fun <T : Any> AdvancedPickerDialog(
    modifier: Modifier,
    shape: Shape = CardDefaults.shape,
    colors: CardColors = CardDefaults.cardColors(),
    elevation: CardElevation = CardDefaults.cardElevation(),
    border: BorderStroke? = null,
    searchFieldState: SearchFieldState? = rememberSearchFieldState(),
    searchPlaceholder: (@Composable () -> Unit)? = null,
    searchSingleLine: Boolean = true,
    caseMatcher: Boolean = true,
    wordMatcher: Boolean = true,
    regexMatcher: Boolean = true,
    compareMatchers: List<Int> = listOf(0, 1, 2, 3, -3, -2, -1),
    items: List<PickerItem<T>>,
    currentItem: PickerItem<T> = items.first(),
    itemText: (T) -> String = T::toString,
    onItemClick: (PickerItem<T>) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var currentItemState by remember { mutableStateOf(currentItem) }

    val matcher: ((String, String) -> Boolean)? = searchFieldState?.matcher

    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Card(
                modifier,
                shape,
                colors,
                elevation,
                border,
        ) {
            Column {
                if (searchFieldState != null) {
                    Box(
                            modifier = Modifier
                                    .background(
                                            color = Color.White.copy(alpha = 0.1f),
                                    ),
                    ) {
                        val equalityMatcher =
                            (searchFieldState.compareMatch == 0 || searchFieldState.compareMatch == -3)

                        AdvancedSearchField(
                            searchFieldState,
                            Modifier.fillMaxWidth(),
                            placeholder = searchPlaceholder,
                            singleLine = searchSingleLine,
                            textStyle = TextStyle(textAlign = TextAlign.Start),
                            caseMatcher = caseMatcher && equalityMatcher,
                            wordMatcher = wordMatcher && equalityMatcher,
                            regexMatcher = regexMatcher && equalityMatcher,
                            compareMatchers = compareMatchers,
                        )
                        HorizontalDivider(thickness = 1.dp)
                    }
                }
                LazyColumn {
                    items(
                            (if (searchFieldState?.query?.isEmpty() != false) {
                                items
                            }
                            else {
                                items.filter {
                                    matcher!!(itemText(it.value!!), searchFieldState.query)
                                }
                            }),
                    ) { item ->
                        Row(
                                Modifier
                                        .fillMaxWidth()
                                        .padding(
                                                horizontal = 18.dp,
                                                vertical = 18.dp,
                                        )
                                        .clickable {
                                            onItemClick(item)
                                            currentItemState = item
                                            onDismissRequest()
                                        },
                        ) {
                            item.icon?.invoke(Modifier)
                            item.text?.invoke(Modifier)
                        }
                    }
                }
            }
        }
    }
}

