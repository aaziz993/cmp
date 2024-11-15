package ai.tech.core.presentation.component.lazycolumn.crud

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.database.crud.findPager
import ai.tech.core.data.expression.Equals
import ai.tech.core.presentation.component.lazycolumn.LazyPagingColumn
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDTableLocalization
import ai.tech.core.presentation.component.lazycolumn.crud.model.CRUDTableState
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import app.cash.paging.compose.collectAsLazyPagingItems
import compose.icons.SimpleIcons
import compose.icons.simpleicons.Microsoftexcel
import androidx.compose.material3.Icon
import arrow.core.prependTo

@Composable
public fun <ID : Any, T : Any> CRUDTable(
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
    getItemId: (T) -> ID,
    getProperties: (T) -> List<String> = { it::class.ser },
    getValues: (T) -> List<Any?>,
    repository: CRUDRepository<T>,
    localization: CRUDTableLocalization = CRUDTableLocalization(),
    onDownload: ((List<T>) -> Unit)? = null,
    onUpload: (() -> Unit)? = null,
    onSave: (insert: List<T>, update: List<T>) -> Unit,
    onDelete: (List<Equals>) -> Unit,
) {
    val data by rememberUpdatedState(repository.findPager(state.sort, state.predicate(), state.limitOffset).flow.collectAsLazyPagingItems())

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
        beforeData = {
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
}


