package ai.tech.core.data.paging

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.RemoteMediator
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalPagingApi::class)
public abstract class AbstractRefreshableMutablePager<Key : Any, Value : Any, Mutation : Any>(
    config: PagingConfig,
    initialKey: Key?,
    remoteMediator: RemoteMediator<Key, Value>?,
    cacheCoroutineScope: CoroutineScope?,
) : AbstractMutablePager<Key, Value, Mutation>(
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) {

    public fun refresh(): Unit = pagingSource.invalidate()
}
