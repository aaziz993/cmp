package ai.tech.core.data.paging

import ai.tech.core.misc.type.letIf
import androidx.compose.runtime.Composable
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.PagingSource
import app.cash.paging.RemoteMediator
import app.cash.paging.cachedIn
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.compose.collectAsLazyPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine

@OptIn(ExperimentalPagingApi::class)
public abstract class MutablePager<Key : Any, Value : Any, Mutation : Any>(
    public val config: PagingConfig,
    public val initialKey: Key? = null,
    remoteMediator: RemoteMediator<Key, Value>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
) {

    protected val mutations: MutableStateFlow<List<Mutation>> = MutableStateFlow(emptyList())

    private lateinit var pagingSource: PagingSource<Key, Value>

    private val data: Flow<PagingData<Mutation>> by lazy {
        Pager(config, initialKey, remoteMediator) { createPagingSource().also { pagingSource = it } }
            .flow
            .letIf({ remoteMediator == null }) { it.cachedIn(cacheCoroutineScope!!) }
            .combine(mutations, ::mergeMutations)
    }

    protected abstract fun mergeMutations(pagingData: PagingData<Value>, mutations: List<Mutation>): PagingData<Mutation>

    protected abstract fun createPagingSource(): PagingSource<Key, Value>

    @Composable
    public fun collectAsLazyPagingItems(): LazyPagingItems<Mutation> = data.collectAsLazyPagingItems()

    public fun refresh(): Unit = pagingSource.invalidate()
}
