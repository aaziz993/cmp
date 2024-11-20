package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.RemoteMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.toList

@OptIn(ExperimentalPagingApi::class)
public fun <Value : Any> CRUDRepository<Value>.pagingSource(
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    firstItemOffset: Int = 0,
    disablePrepend: Boolean = false,
): PagingSource<Int, Value> = CRUDPagingSource({ find(sort, predicate, it).toList() }, firstItemOffset, disablePrepend)

@OptIn(ExperimentalPagingApi::class)
public fun <Value : Any> CRUDRepository<Value>.pager(
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, Value>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
    firstItemOffset: Int = 0,
    disablePrepend: Boolean = false,
): CRUDRefreshablePager<Value> = CRUDRefreshablePager(
    sort,
    predicate,
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) { sort, predicate -> pagingSource(sort, predicate, firstItemOffset, disablePrepend) }

@OptIn(ExperimentalPagingApi::class)
public fun <Value : Any> CRUDRepository<Value>.mutablePager(
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    create: (id: Any) -> Value,
    properties: List<EntityProperty>,
    getValues: (Value) -> List<Any?>,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, Value>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
    firstItemOffset: Int = 0,
    disablePrepend: Boolean = false,
): CRUDRefreshableMutablePager<Value> = CRUDRefreshableMutablePager(
    sort,
    predicate,
    create,
    properties,
    getValues,
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) { sort, predicate -> pagingSource(sort, predicate, firstItemOffset, disablePrepend) }

@OptIn(ExperimentalPagingApi::class)
public fun CRUDRepository<*>.pagingSource(
    projections: List<Variable>,
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    firstItemOffset: Int = 0,
    disablePrepend: Boolean = false,
): PagingSource<Int, List<Any?>> = CRUDPagingSource({ find(projections, sort, predicate, it).toList() }, firstItemOffset, disablePrepend)

@OptIn(ExperimentalPagingApi::class)
public fun CRUDRepository<*>.pager(
    projections: List<Variable>,
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, List<Any?>>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
    firstItemOffset: Int = 0,
    disablePrepend: Boolean = false,
): CRUDProjectionRefreshablePager = CRUDProjectionRefreshablePager(
    projections,
    sort,
    predicate,
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) { projections, sort, predicate -> pagingSource(projections, sort, predicate, firstItemOffset, disablePrepend) }

@OptIn(ExperimentalPagingApi::class)
public fun CRUDRepository<*>.mutablePager(
    projections: List<Variable>,
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    create: (id: Any) -> List<Any?>,
    properties: List<EntityProperty>,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, List<Any?>>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
    firstItemOffset: Int = 0,    disablePrepend: Boolean = false,

): CRUDProjectionRefreshableMutablePager = CRUDProjectionRefreshableMutablePager(
    projections,
    sort,
    predicate,
    create,
    properties,
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) { projections, sort, predicate -> pagingSource(projections, sort, predicate, firstItemOffset, disablePrepend) }
