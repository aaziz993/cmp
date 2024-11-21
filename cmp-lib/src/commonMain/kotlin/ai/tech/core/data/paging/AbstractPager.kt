package ai.tech.core.data.paging

import ai.tech.core.misc.type.letIf
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.PagingSource
import app.cash.paging.RemoteMediator
import app.cash.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

public abstract class AbstractPager<Key : Any, Value : Any> @OptIn(ExperimentalPagingApi::class) constructor(
    public val config: PagingConfig,
    public val initialKey: Key? = null,
    private val remoteMediator: RemoteMediator<Key, Value>?,
    private val cacheCoroutineScope: CoroutineScope?,
) {

    protected lateinit var pagingSource: PagingSource<Key, Value>

    @OptIn(ExperimentalPagingApi::class)
    public val data: Flow<PagingData<Value>> by lazy {
        Pager(config, initialKey, remoteMediator) { createPagingSource().also { pagingSource = it } }
            .flow.letIf({ remoteMediator == null && cacheCoroutineScope != null }) { it.cachedIn(cacheCoroutineScope!!) }
    }

    protected abstract fun createPagingSource(): PagingSource<Key, Value>

    protected open fun refresh(): Unit = pagingSource.invalidate()
}
