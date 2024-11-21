package ai.tech.core.data.paging

import ai.tech.core.misc.type.letIf
import app.cash.paging.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine

@OptIn(ExperimentalPagingApi::class)
public abstract class AbstractMutablePager<Key : Any, Value : Any, Mutation : Any>(
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

    protected val mutations: MutableStateFlow<List<Mutation>> = MutableStateFlow(emptyList())

    public val mutatedData: Flow<PagingData<Mutation>> by lazy {
        data.letIf({ remoteMediator == null }) { it.cachedIn(cacheCoroutineScope!!) }
            .combine(mutations, ::mergeMutations)
    }

    protected abstract fun mergeMutations(pagingData: PagingData<Value>, mutations: List<Mutation>): PagingData<Mutation>
}
