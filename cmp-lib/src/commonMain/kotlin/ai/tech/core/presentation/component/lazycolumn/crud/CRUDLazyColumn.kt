package ai.tech.core.presentation.component.lazycolumn.crud

import ai.tech.core.data.expression.Equals
import ai.tech.core.presentation.component.lazycolumn.LazyPagingColumn
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDTableLocalization
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDTableState
import ai.tech.core.presentation.component.lazycolumn.crud.viewmodel.CRUDViewModel
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Microsoftexcel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
public fun <T : Any> CRUDTable(
    modifier: Modifier = Modifier.fillMaxSize(),
    state: CRUDTableState,
    contentPadding: PaddingValues = PaddingValues(16.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    title: String? = null,
    downloadIcon: @Composable () -> Unit = { Icon(SimpleIcons.Microsoftexcel, null, tint = Color(0xFF33A852)) },
    downloadAllIcon: @Composable () -> Unit = { Icon(SimpleIcons.Microsoftexcel, null, tint = Color(0xFF33A852)) },
    getItemId: (T) -> Any,
    getProperties: (T) -> List<String>,
    getValues: (T) -> List<Any?>,
    crudViewModel: CRUDViewModel<T>,
    localization: CRUDTableLocalization = CRUDTableLocalization(),
    onDownload: ((List<T>) -> Unit)? = null,
    onUpload: (() -> Unit)? = null,
    onSave: (insert: List<T>, update: List<T>) -> Unit,
    onDelete: (List<Equals>) -> Unit,
) {

    val data = crudViewModel.state.collectAsLazyPagingItems()

    val scope = rememberCoroutineScope()

    LazyPagingColumn(
        modifier,
        rememberLazyListState(),
        contentPadding,
        reverseLayout,
        verticalArrangement,
        horizontalAlignment,
        flingBehavior,
        userScrollEnabled,
        beforeItems = {
            item {
                title?.let { TitleRow(contentPadding, it) }
                OptionRow(state, contentPadding, localization)
                HeaderRow()
            }
        },
        data = data,
    ) {
        DataRow(it)
    }

    LaunchedEffect(key1 = Unit) {
        launch(Dispatchers.Main) {
            data.refresh()
        }
    }
}


