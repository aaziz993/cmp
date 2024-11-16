package ai.tech.core.data.database.crud

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.database.paging.DataPagingSource
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import app.cash.paging.COUNT_UNDEFINED
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.MAX_SIZE_UNBOUNDED
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.RemoteMediator

@OptIn(ExperimentalPagingApi::class)
public class PagingCRUDRepository<T : Any>(
    repository: CRUDRepository<T>,
    public val firstItemOffset: Long = 0,
    public val prefetchDistance: Int = 10,
    public val enablePlaceholders: Boolean = true,
    public val maxSize: Int = MAX_SIZE_UNBOUNDED,
    public val jumpThreshold: Int = COUNT_UNDEFINED,
) : CRUDRepository<T> by repository {

    public fun findPager(
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
        limitOffset: LimitOffset,
        remoteMediator: RemoteMediator<Long, T>? = null,
    ): Pager<Long, T> = Pager(
        PagingConfig(limitOffset.limit.toInt(), prefetchDistance, enablePlaceholders, limitOffset.limit.toInt(), maxSize, jumpThreshold),
        limitOffset.offset,
        remoteMediator,
    ) {
        DataPagingSource({ find(sort, predicate, it) }, firstItemOffset)
    }

    public fun findPager(
        projections: List<Variable>,
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
        limitOffset: LimitOffset,
        remoteMediator: RemoteMediator<Long, List<Any?>>? = null,
    ): Pager<Long, List<Any?>> = Pager(
        PagingConfig(limitOffset.limit.toInt(), prefetchDistance, enablePlaceholders, limitOffset.limit.toInt(), maxSize, jumpThreshold),
        limitOffset.offset,
        remoteMediator,
    ) {
        DataPagingSource({ find(projections, sort, predicate, it) }, firstItemOffset)
    }
}

public fun <T : Any> CRUDRepository<T>.asPagingRepository(
    firstItemOffset: Long,
    prefetchDistance: Int = 10,
    enablePlaceholders: Boolean = true,
    maxSize: Int = MAX_SIZE_UNBOUNDED,
    jumpThreshold: Int = COUNT_UNDEFINED,
): PagingCRUDRepository<T> = PagingCRUDRepository(this, firstItemOffset, prefetchDistance, enablePlaceholders, maxSize, jumpThreshold)

