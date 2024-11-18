package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.RemoteMediator
import kotlinx.coroutines.flow.toList

@OptIn(ExperimentalPagingApi::class)
public fun <Value : Any> CRUDRepository<Value>.findPagingSource(
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    firstItemOffset: Int = 0,
): PagingSource<Int, Value> = CRUDPagingSource({ find(sort, predicate, it).toList() }, firstItemOffset)

@OptIn(ExperimentalPagingApi::class)
public fun <Value : Any> CRUDRepository<Value>.findPager(
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, Value>? = null,
    firstItemOffset: Int = 0,
): Pager<Int, Value> = Pager(
    config,
    initialKey,
    remoteMediator,
) { findPagingSource(sort, predicate, firstItemOffset) }

@OptIn(ExperimentalPagingApi::class)
public fun CRUDRepository<*>.findPagingSource(
    projections: List<Variable>,
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    firstItemOffset: Int = 0,
): PagingSource<Int, List<Any?>> = CRUDPagingSource({ find(projections, sort, predicate, it).toList() }, firstItemOffset)

@OptIn(ExperimentalPagingApi::class)
public fun CRUDRepository<*>.findPager(
    projections: List<Variable>,
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, List<Any?>>? = null,
    firstItemOffset: Int = 0,
): Pager<Int, List<Any?>> = Pager(
    config,
    initialKey,
    remoteMediator,
) { findPagingSource(projections, sort, predicate, firstItemOffset) }
