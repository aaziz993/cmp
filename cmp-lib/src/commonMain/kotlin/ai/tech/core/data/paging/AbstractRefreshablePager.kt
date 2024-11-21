package ai.tech.core.data.paging

import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.RemoteMediator
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalPagingApi::class)
public abstract class AbstractRefreshablePager<Key : Any, Value : Any>(
    config: PagingConfig,
    initialKey: Key?,
    remoteMediator: RemoteMediator<Key, Value>?,
    cacheCoroutineScope: CoroutineScope?,
) : AbstractPager<Key, Value>(
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) {

    public final override fun refresh(): Unit = super.refresh()
}
