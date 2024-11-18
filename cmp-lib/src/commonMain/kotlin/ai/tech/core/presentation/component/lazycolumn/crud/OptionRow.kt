package ai.tech.core.presentation.component.lazycolumn.crud

import ai.tech.core.presentation.component.column.expandable.ExpandableSection
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDTableLocalization
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun OptionRow(
    state: CRUDLazyColumnState,
    contentPadding: PaddingValues,
    localization: CRUDTableLocalization,
) = ExpandableSection(
    Modifier.padding(contentPadding).fillMaxWidth(),
    { Text(localization.options, style = MaterialTheme.typography.titleMedium) },
) {
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(localization.multiSort)
            Switch(state.isMultiSort, { state.isMultiSort = it })
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(localization.liveSearch)
            Switch(state.isLiveSearch, { state.isLiveSearch = it })
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(localization.liveSearch)
            Switch(state.isLiveSearch, { state.isLiveSearch = it })
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(localization.pagination)
            Switch(checked = state.showPagination, onCheckedChange = { state.showPagination = it })
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(localization.actions)
            Switch(checked = state.showActions, onCheckedChange = { state.showActions = it })
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(localization.select)
            Switch(checked = state.showSelect, onCheckedChange = { state.showSelect = it })
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(localization.header)
            Switch(checked = state.showHeader, onCheckedChange = { state.showHeader = it })
        }

        if (state.showHeader) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(localization.search)
                Switch(checked = state.showSearch, onCheckedChange = { state.showSearch = it })
            }
        }
    }
}
