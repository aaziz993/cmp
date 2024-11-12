package ai.tech.core.data.database.crud

import ai.tech.core.data.database.crud.model.LimitOffset
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.PagingSourceLoadParams
import app.cash.paging.PagingSourceLoadResult
import app.cash.paging.PagingSourceLoadResultError
import app.cash.paging.PagingSourceLoadResultPage
import app.cash.paging.PagingState

public class CRUDPagingSource<T : Any>(private val repository: CRUDRepository<T>) : PagingSource<Long, T>() {

    override fun getRefreshKey(state: PagingState<Long, T>): Long? = state.anchorPosition?.toLong()

    override suspend fun load(params: PagingSourceLoadParams<Long>): PagingSourceLoadResult<Long, T> {
        val offset = params.key ?: 0L
        val limit = params.loadSize.toLong()

        return try {
            val page = repository.find(limitOffset = LimitOffset(offset, limit))

            return PagingSourceLoadResultPage(
                page.entities,
                (offset - 1L).takeIf { it > -1 },
                (offset + 1L).takeIf { it < page.totalCount },
            )
        }
        catch (e: Exception) {
            return PagingSourceLoadResultError<Long, T>(e)
        }
    }
}

