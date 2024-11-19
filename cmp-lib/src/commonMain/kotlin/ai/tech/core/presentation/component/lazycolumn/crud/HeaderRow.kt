package ai.tech.core.presentation.component.lazycolumn.crud

import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.model.MutationItem
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnLocalization
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnState
import ai.tech.core.presentation.component.textfield.model.TextField
import ai.tech.core.presentation.component.textfield.search.AdvancedSearchField
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldCompare
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.LayoutDirection
import compose.icons.EvaIcons
import compose.icons.LineAwesomeIcons
import compose.icons.evaicons.Outline
import compose.icons.evaicons.outline.Search
import compose.icons.lineawesomeicons.SortDownSolid
import compose.icons.lineawesomeicons.SortSolid
import compose.icons.lineawesomeicons.SortUpSolid
import kotlinx.serialization.ExperimentalSerializationApi

@OptIn(ExperimentalSerializationApi::class)
@Composable
internal fun <T : Any> HeaderRow(
    contentPadding: PaddingValues,
    state: CRUDLazyColumnState,
    properties: List<EntityProperty>,
    items: List<MutationItem<T>>,
    localization: CRUDLazyColumnLocalization,
    onSelect: () -> Unit,
    onSearch: () -> Unit,
) {
    if (state.showHeader) {
        Row(
            Modifier.padding(contentPadding).fillMaxWidth(), verticalAlignment = Alignment.Top,
        ) {
            if (state.showSelect) {
                Checkbox(items.all(MutationItem<T>::isSelected), { onSelect() })
            }

            val searchLeftPadding = contentPadding.calculateStartPadding(LayoutDirection.Ltr) / 4
            val searchRightPadding = contentPadding.calculateEndPadding(LayoutDirection.Ltr) / 4

            properties.forEachIndexed { index, property ->
                Column(
                    Modifier.wrapContentHeight().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Row(
                        Modifier.wrapContentWidth(), verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            property.header, maxLines = 1, style = MaterialTheme.typography.titleSmall,
                        )

                        val order = state.getOrder(property)

                        IconButton({ state.order(property) }) {
                            Icon(
                                order?.let {
                                    if (it.value.ascending) {
                                        LineAwesomeIcons.SortUpSolid
                                    }
                                    else {
                                        LineAwesomeIcons.SortDownSolid
                                    }
                                } ?: LineAwesomeIcons.SortSolid,
                                null,
                            )
                        }

                        order?.takeIf { state.sort.size > 1 }?.let {
                            Text(
                                it.index.toString(), maxLines = 1, style = MaterialTheme.typography.titleSmall,
                            )
                        }
                    }

                    val searchFieldState = state.searchFieldStates[index]

                    if (state.showSearch) {
                        when (val textField = TextField(property.descriptor)) {
                            TextField.Text -> AdvancedSearchField(
                                searchFieldState,
                                Modifier.padding(start = searchLeftPadding, end = searchRightPadding).fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "${localization.search}...",
                                        maxLines = 1,
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                },
                                singleLine = true,
                                outlined = true,
                                wordMatcher = searchFieldState.compareMatch != SearchFieldCompare.NOT_EQUAL,
                                caseMatcher = !(searchFieldState.compareMatch == SearchFieldCompare.NOT_EQUAL || searchFieldState.wordMatch),
                                regexMatcher = false,
                                compareMatchers = listOf(SearchFieldCompare.EQUALS, SearchFieldCompare.NOT_EQUAL),
                            )

                            else -> AdvancedSearchField(
                                searchFieldState,
                                Modifier.padding(start = searchLeftPadding, end = searchRightPadding).fillMaxWidth(),
                                placeholder = {
                                    Text(
                                        "${localization.search}...",
                                        maxLines = 1,
                                        style = MaterialTheme.typography.bodySmall,
                                    )
                                },
                                singleLine = true,
                                type = textField,
                                outlined = true,
                                caseMatcher = false,
                                wordMatcher = false,
                                regexMatcher = false,
                            )
                        }
                    }
                }
            }

            if (state.showActions) {
                Column(
                    Modifier.wrapContentHeight().weight(.4f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        localization.actions, maxLines = 1, style = MaterialTheme.typography.titleSmall,
                    )
                    IconButton(onSearch) { Icon(EvaIcons.Outline.Search, null) }
                }
            }
        }
    }
}
