package ai.tech.core.presentation.component.lazycolumn.paging

import ai.tech.core.presentation.component.lazycolumn.model.LazyPagingColumnLocalization
import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.ScrollableDefaults
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.itemContentType
import app.cash.paging.compose.itemKey
import kotlin.jvm.JvmSuppressWildcards

@Composable
public fun <T : Any> LazyPagingColumn(
    modifier: Modifier = Modifier.fillMaxSize(),
    state: LazyListState = rememberLazyListState(),
    contentPadding: PaddingValues = PaddingValues(16.dp),
    reverseLayout: Boolean = false,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(8.dp),
    horizontalAlignment: Alignment.Horizontal = Alignment.CenterHorizontally,
    flingBehavior: FlingBehavior = ScrollableDefaults.flingBehavior(),
    userScrollEnabled: Boolean = true,
    beforePrepend: LazyListScope.() -> Unit = {},
    beforeItems: LazyListScope.() -> Unit = {},
    afterItems: LazyListScope.() -> Unit = {},
    afterAppend: LazyListScope.() -> Unit = {},
    getRefreshErrorMsg: (Throwable) -> String? = { null },
    getAddErrorMsg: (Throwable) -> String? = { null },
    data: LazyPagingItems<T>,
    itemKey: ((T) -> Any)? = null,
    itemContentType: ((item: @JvmSuppressWildcards T) -> Any?)? = null,
    localization: LazyPagingColumnLocalization = LazyPagingColumnLocalization(),
    content: @Composable (T) -> Unit,
) {
    LazyColumn(
        modifier,
        state,
        contentPadding,
        reverseLayout,
        verticalArrangement,
        horizontalAlignment,
        flingBehavior,
        userScrollEnabled,
    ) {
        beforePrepend()
        when (data.loadState.prepend) {
            LoadStateLoading -> {
                item {
                    CircularProgressIndicator(
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth(1f)
                            .padding(20.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                    )
                }
            }

            is LoadStateError -> {
                getAddErrorMsg((data.loadState.prepend as LoadStateError).error)?.let { text ->
                    item {
                        AddLoadError(
                            message = text,
                            onClickRetry = data::retry,
                        )
                    }
                }
            }

            else -> Unit
        }

        beforeItems()

        when (data.loadState.refresh) {
            is LoadStateNotLoading -> {
                if (data.itemCount > 0) {
                    items(
                        data.itemCount,
                        itemKey?.let(data::itemKey),
                        data.itemContentType(itemContentType),
                    ) { index ->
                        data[index]?.let { content(it) }
                    }
                }
                else {
                    localization.noItems?.let { text ->
                        item {
                            Box(
                                modifier = Modifier.fillMaxWidth(1f),
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    text = text,
                                    modifier = Modifier.align(Alignment.Center),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }
                    }
                }
            }

            LoadStateLoading -> {
                item {
                    Box(
                        modifier = Modifier.fillMaxSize().padding(20.dp),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator(
                            color = Color.Red,
                        )
                    }
                }
            }

            is LoadStateError -> {
                getRefreshErrorMsg((data.loadState.refresh as LoadStateError).error)?.let { text ->
                    item {
                        RefreshLoadError(
                            message = text,
                            onClickRetry = data::retry,
                            modifier = Modifier.fillMaxWidth(1f),
                        )
                    }
                }
            }

            else -> Unit
        }

        afterItems()

        when (data.loadState.append) {
            LoadStateLoading -> {
                item {
                    CircularProgressIndicator(
                        color = Color.Red,
                        modifier = Modifier.fillMaxWidth(1f)
                            .padding(20.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally),
                    )
                }
            }

            is LoadStateError -> {
                getAddErrorMsg((data.loadState.append as LoadStateError).error)?.let { text ->
                    item {
                        AddLoadError(
                            message = text,
                            onClickRetry = data::retry,
                        )
                    }
                }
            }

            else -> Unit
        }

        afterAppend()
    }
}
