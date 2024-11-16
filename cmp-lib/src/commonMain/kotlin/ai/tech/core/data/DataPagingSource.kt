package ai.tech.core.data

import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Page
import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState

public class DataPagingSource<Value : Any>(private val fetchData: suspend (LimitOffset) -> Page<Value>, public val initialOffset: Long = 0) : PagingSource<Long, Value>() {

    @Suppress("UNCHECKED_CAST")
    override suspend fun load(params: PagingSourceLoadParams<Long>): PagingSourceLoadResult<Long, Value> {
        val offset = params.key ?: initialOffset
        val limit = params.loadSize.toLong()

        return try {
            val page = fetchData(LimitOffset(offset, limit))

            PagingSourceLoadResultPage(
                page.entities,
                offset.dec().takeIf { it >= initialOffset },
                offset.inc().takeIf { it < page.totalCount },
            )
        }
        catch (e: Exception) {
            PagingSourceLoadResultError<Long, Value>(e)
        } as PagingSourceLoadResult<Long, Value>
    }

    override fun getRefreshKey(state: PagingState<Long, Value>): Long? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }
}
