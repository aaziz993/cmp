package ai.tech.core.presentation.component.lazycolumn.crud

import ai.tech.core.data.expression.Equals
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnLocalization
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnState
import ai.tech.core.presentation.component.lazycolumn.crud.viewmodel.CRUDAction
import ai.tech.core.presentation.component.lazycolumn.crud.viewmodel.CRUDViewModel
import ai.tech.core.presentation.component.lazycolumn.paging.LazyPagingColumn
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.cash.paging.compose.collectAsLazyPagingItems
import com.ionspin.kotlin.bignum.integer.Quadruple
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Microsoftexcel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter

@OptIn(FlowPreview::class)
@Composable
public fun <T : Any> CRUDLazyColumn(
    modifier: Modifier = Modifier.fillMaxSize(),
    state: CRUDLazyColumnState,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    readOnly: Boolean = false,
    title: String? = null,
    downloadIcon: @Composable () -> Unit = { Icon(SimpleIcons.Microsoftexcel, null, tint = Color(0xFF33A852)) },
    downloadAllIcon: @Composable () -> Unit = { Icon(SimpleIcons.Microsoftexcel, null, tint = Color(0xFF33A852)) },
    getHeader: (String) -> String? = { it },
    viewModel: CRUDViewModel<T>,
    localization: CRUDLazyColumnLocalization = CRUDLazyColumnLocalization(),
    onDownload: ((List<T>) -> Unit)? = null,
): Unit = Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    title?.let { TitleRow(contentPadding, it) }

    Spacer(modifier = Modifier.height(10.dp))

    val data = viewModel.pager.mutatedData.collectAsLazyPagingItems()

    val items = data.itemSnapshotList.items

    ActionRow(
        contentPadding,
        readOnly,
        downloadAllIcon,
        viewModel.pager.properties,
        items,
        localization,
        onDownload,
        {},
        { viewModel.action(CRUDAction.New) },
        { viewModel.action(CRUDAction.NewFromSelected) },
        { viewModel.action(CRUDAction.RemoveSelected) },
        { viewModel.action(CRUDAction.EditSelected) },
        { viewModel.action(CRUDAction.SaveSelected) },
    ) { viewModel.action(CRUDAction.DeleteSelected) }

    val headers = viewModel.pager.properties.mapNotNull { getHeader(it.name) }

    HeaderRow(
        contentPadding,
        state,
        headers,
        viewModel.pager.properties,
        items,
        localization,
        { viewModel.action(CRUDAction.SelectAll(it)) },
        { viewModel.action(CRUDAction.UnselectAll) },
    ) { viewModel.action(CRUDAction.Load(state.sort, state.searchFieldStates)) }

    LazyPagingColumn(
        Modifier.fillMaxSize(),
        rememberLazyListState(),
        contentPadding,
        reverseLayout,
        verticalArrangement,
        horizontalAlignment,
        flingBehavior,
        userScrollEnabled,
        data = data,
    ) {
        DataRow(
            state,
            readOnly,
            downloadAllIcon,
            viewModel.pager.properties,
            it,
            if (onDownload == null) {
                null
            }
            else {
                { onDownload(listOf(it)) }
            },
            { viewModel.action(CRUDAction.NewFrom(it)) },
            { viewModel.action(CRUDAction.Remove(listOf(it))) },
            { viewModel.action(CRUDAction.EditOrUnEdit(it)) },
            { viewModel.action(CRUDAction.Save(it)) },
            { viewModel.action(CRUDAction.Delete(it)) },
            { viewModel.action(CRUDAction.SelectOrUnselect(it)) },
        ) { id, index, value -> viewModel.action(CRUDAction.SetValue(id, index, value)) }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            state.searchFieldStates.map { it.query }
        }.filter { state.isLiveSearch }.debounce(1000L).collect {
            viewModel.action(CRUDAction.Load(state.sort, state.searchFieldStates))
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow {
            state.searchFieldStates.map { Quadruple(it.caseMatch, it.wordMatch, it.regexMatch, it.compareMatch) }
        }.filter { state.isLiveSearch }.collect {
            viewModel.action(CRUDAction.Load(state.sort, state.searchFieldStates))
        }
    }

    LaunchedEffect(Unit) {
        snapshotFlow { state.sort.toList() }.filter { state.isLiveSearch }.collect { viewModel.action(CRUDAction.Load(state.sort, state.searchFieldStates)) }
    }
}


