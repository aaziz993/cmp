package ai.tech.core.presentation.component.lazycolumn.crud

import ai.tech.core.data.validator.Validator
import ai.tech.core.misc.type.ifNull
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnLocalization
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnState
import ai.tech.core.presentation.component.lazycolumn.crud.viewmodel.CRUDAction
import ai.tech.core.presentation.component.lazycolumn.crud.viewmodel.CRUDViewModel
import ai.tech.core.presentation.component.lazycolumn.paging.LazyPagingColumn
import ai.tech.core.presentation.component.textfield.AdvancedTextField
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import com.ionspin.kotlin.bignum.integer.Quadruple
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Microsoftexcel
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@OptIn(FlowPreview::class, ExperimentalFoundationApi::class)
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
    onDownload: (List<T>) -> Unit = {},
): Unit = Column {
    title?.let { TitleRow(contentPadding, it) }

    Spacer(modifier = Modifier.height(10.dp))

    val data = viewModel.pager.mutatedData.collectAsLazyPagingItems()

    val items = data.itemSnapshotList.items

    OptionRow(
        state,
        contentPadding,
        localization,
    )

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

    LazyPagingColumn(
        modifier,
        rememberLazyListState(),
        contentPadding,
        reverseLayout,
        verticalArrangement,
        horizontalAlignment,
        flingBehavior,
        userScrollEnabled,
        {
            stickyHeader {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HeaderRow(
                        contentPadding,
                        state,
                        headers,
                        viewModel.pager.properties,
                        items,
                        localization,
                        { viewModel.action(CRUDAction.SelectAll(it)) },
                        { viewModel.action(CRUDAction.UnselectAll) },
                    ) { viewModel.action(CRUDAction.Find(state.sort, state.searchFieldStates)) }
                }
            }
        },
        data = data,
    ) {
        DataRow(
            state,
            readOnly,
            downloadAllIcon,
            viewModel.pager.properties,
            it,
            { onDownload(listOf(it)) },
            { viewModel.action(CRUDAction.NewFrom(it)) },
            { viewModel.action(CRUDAction.Remove(listOf(it))) },
            { viewModel.action(CRUDAction.EditOrUnEdit(it)) },
            { viewModel.action(CRUDAction.Save(it)) },
            { viewModel.action(CRUDAction.Delete(it)) },
            { viewModel.action(CRUDAction.SelectOrUnselect(it)) },
        ) { id, index, value -> viewModel.action(CRUDAction.SetValue(id, index, value)) }
    }

    LaunchedEffect(state.isLiveSearch, state.liveSearchDebounce) {
        if (state.isLiveSearch) {
            snapshotFlow {
                state.searchFieldStates.map { it.query }
            }.debounce(Duration.parseOrNull(state.liveSearchDebounce).ifNull { 1.toDuration(DurationUnit.SECONDS) }.inWholeMilliseconds).collect {
                viewModel.action(CRUDAction.Find(state.sort, state.searchFieldStates))
            }
        }
    }

    LaunchedEffect(state.isLiveSearch) {
        if (state.isLiveSearch) {
            snapshotFlow {
                state.searchFieldStates.map { Quadruple(it.caseMatch, it.wordMatch, it.regexMatch, it.compareMatch) }
            }.collect {
                viewModel.action(CRUDAction.Find(state.sort, state.searchFieldStates))
            }
        }
    }

    LaunchedEffect(state.isLiveSearch) {
        if (state.isLiveSearch) {
            snapshotFlow { state.sort.toList() }.collect { viewModel.action(CRUDAction.Find(state.sort, state.searchFieldStates)) }
        }
    }
}


