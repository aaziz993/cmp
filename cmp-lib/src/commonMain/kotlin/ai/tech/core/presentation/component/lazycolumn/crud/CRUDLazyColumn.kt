package ai.tech.core.presentation.component.lazycolumn.crud

import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.expression.Equals
import ai.tech.core.presentation.component.lazycolumn.paging.LazyPagingColumn
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnLocalization
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDLazyColumnState
import ai.tech.core.data.crud.client.model.MutationItem
import ai.tech.core.presentation.component.lazycolumn.crud.viewmodel.CRUDAction
import ai.tech.core.presentation.component.lazycolumn.crud.viewmodel.CRUDViewModel
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Microsoftexcel

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
    getHeader: (String) -> String = { it },
    viewModel: CRUDViewModel<T>,
    localization: CRUDLazyColumnLocalization = CRUDLazyColumnLocalization(),
    onDownload: ((List<T>) -> Unit)? = null,
    onUpload: (() -> Unit)? = null,
    onSave: (insert: List<T>, update: List<T>) -> Unit,
    onDelete: (List<Equals>) -> Unit,
): Unit = Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
    title?.let { TitleRow(contentPadding, it) }

    Spacer(modifier = Modifier.height(10.dp))

    val data = viewModel.pager.collectAsLazyPagingItems()

    val items = data.itemSnapshotList.items

    ActionRow(
        contentPadding,
        readOnly,
        downloadAllIcon,
        viewModel.properties,
        items,
        localization,
        onDownload?.let { { it(items.filter { it.isSelected && !it.isNew }.map(MutationItem<T>::entity)) } },
        onUpload,
        { viewModel.action(CRUDAction.EditSelected) },
        { viewModel.action(CRUDAction.Add) },
        { viewModel.action(CRUDAction.CopySelected) },
        { viewModel.action(CRUDAction.RemoveSelected) },
        { viewModel.action(CRUDAction.SaveSelected) },
    ) { viewModel.action(CRUDAction.DeleteSelected) }

    HeaderRow(
        contentPadding,
        state,
        viewModel.properties,
        items,
        localization,
        { viewModel.action(CRUDAction.SelectAll) },
    ) { viewModel.action(CRUDAction.Find(state.sort, state.searchFieldStates, LimitOffset(0, 10))) }

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
        val id = it.id
        val entity = it.entity
        DataRow(
            state,
            readOnly,
            downloadAllIcon,
            viewModel.properties,
            it,
            { viewModel.action(CRUDAction.Select(it)) },
            onDownload?.let { { it(listOf(entity)) } },
            { viewModel.action(CRUDAction.Copy(it)) },
            { viewModel.action(CRUDAction.Remove(id)) },
            { viewModel.action(CRUDAction.Edit(it)) },
            { index, value -> viewModel.action(CRUDAction.ChangeValue(id, index, value)) },
            { viewModel.action(CRUDAction.Save(entity)) },
        ) { viewModel.action(CRUDAction.Delete(id)) }
    }
}


