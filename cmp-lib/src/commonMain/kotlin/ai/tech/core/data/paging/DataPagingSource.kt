package ai.tech.core.data.paging

import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Page
import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState

public class DataPagingSource<Value : Any>(
    private val fetchData: suspend (LimitOffset) -> Page<Value>,
    public val firstItemOffset: Int = 0) : PagingSource<Int, Value>() {

    @Suppress("UNCHECKED_CAST")
    override suspend fun load(params: PagingSourceLoadParams<Int>): PagingSourceLoadResult<Int, Value> {
        val loadKey = params.key ?: 0

        val limit = params.loadSize.toLong()

        val offset = loadKey * limit + firstItemOffset


        return try {
            val data = fetchData(LimitOffset(offset, limit))

            PagingSourceLoadResultPage(
                data.entities,
                loadKey.takeIf { data.entities.isNotEmpty() && it > 0 }?.dec(),
                loadKey.takeIf { data.entities.size >= limit }?.inc(),
            )
        }
        catch (e: Exception) {
            PagingSourceLoadResultError<Int, Value>(e)
        } as PagingSourceLoadResult<Int, Value>
    }

    override fun getRefreshKey(state: PagingState<Int, Value>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
