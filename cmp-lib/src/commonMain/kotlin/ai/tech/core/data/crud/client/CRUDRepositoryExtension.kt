package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.paging.DataPagingSource
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import app.cash.paging.COUNT_UNDEFINED
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.MAX_SIZE_UNBOUNDED
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.RemoteMediator

@OptIn(ExperimentalPagingApi::class)
public fun <T : Any> CRUDRepository<T>.findPager(
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    limitOffset: LimitOffset = LimitOffset(),
    firstItemOffset: Int = 0,
    prefetchDistance: Int = 10,
    enablePlaceholders: Boolean = true,
    maxSize: Int = MAX_SIZE_UNBOUNDED,
    jumpThreshold: Int = COUNT_UNDEFINED,
    remoteMediator: RemoteMediator<Int, T>? = null,
): Pager<Int, T> = Pager(
    PagingConfig(limitOffset.limit.toInt(), prefetchDistance, enablePlaceholders, limitOffset.limit.toInt(), maxSize, jumpThreshold),
    (limitOffset.offset / limitOffset.limit).toInt(),
    remoteMediator,
) {
    DataPagingSource({ find(sort, predicate, it) }, firstItemOffset)
}

@OptIn(ExperimentalPagingApi::class)
public fun <T : Any> CRUDRepository<T>.findPager(
    projections: List<Variable>,
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    limitOffset: LimitOffset = LimitOffset(),
    firstItemOffset: Int = 0,
    prefetchDistance: Int = 10,
    enablePlaceholders: Boolean = true,
    maxSize: Int = MAX_SIZE_UNBOUNDED,
    jumpThreshold: Int = COUNT_UNDEFINED,
    remoteMediator: RemoteMediator<Int, List<Any?>>? = null,
): Pager<Int, List<Any?>> = Pager(
    PagingConfig(limitOffset.limit.toInt(), prefetchDistance, enablePlaceholders, limitOffset.limit.toInt(), maxSize, jumpThreshold),
    (limitOffset.offset / limitOffset.limit).toInt(),
    remoteMediator,
) {
    DataPagingSource({ find(projections, sort, predicate, it) }, firstItemOffset)
}
