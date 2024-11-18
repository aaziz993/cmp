package ai.tech.core.data.paging

import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState

public abstract class AbstractDataPagingSource<Key : Any, Value : Any> : PagingSource<Key, Value>() {

    protected abstract suspend fun fetchData(loadKey: Key?, pageSize: Int): List<Value>

    protected abstract fun getNextKey(loadKey: Key): Key?

    protected abstract fun getPrevKey(loadKey: Key): Key?

    @Suppress("UNCHECKED_CAST")
    final override suspend fun load(params: PagingSourceLoadParams<Key>): PagingSourceLoadResult<Key, Value> {
        val loadKey = params.key

        val pageSize = params.loadSize

        return try {
            val data = fetchData(loadKey, pageSize)

            if (data.size < pageSize) {
                return PagingSourceLoadResultPage(data, null, null)
            }

            PagingSourceLoadResultPage(data, getPrevKey(loadKey), getNextKey(loadKey))
        }
        catch (e: Exception) {
            PagingSourceLoadResultError<Key, Value>(e)
        } as PagingSourceLoadResult<Key, Value>
    }

    final override fun getRefreshKey(state: PagingState<Key, Value>): Key? = state.anchorPosition?.let { anchorPosition ->
        state.closestPageToPosition(anchorPosition)?.prevKey?.let(::getNextKey)
            ?: state.closestPageToPosition(anchorPosition)?.nextKey?.let(::getPrevKey)
    }
}
