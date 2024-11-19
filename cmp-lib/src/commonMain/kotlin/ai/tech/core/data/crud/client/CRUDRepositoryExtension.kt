package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.client.model.EntityColumn
import ai.tech.core.data.crud.client.model.MutationItem
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.RemoteMediator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.toList

@OptIn(ExperimentalPagingApi::class)
public fun <Value : Any> CRUDRepository<Value>.findPagingSource(
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    firstItemOffset: Int = 0,
): PagingSource<Int, Value> = CRUDPagingSource({ find(sort, predicate, it).toList() }, firstItemOffset)

@OptIn(ExperimentalPagingApi::class)
public fun <Value : Any> CRUDRepository<Value>.pager(
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
public fun <Value : Any> CRUDRepository<Value>.mutablePager(
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    create: (id: Any) -> Value,
    properties: List<EntityColumn>,
    getValues: (Value) -> List<Any?>,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, Value>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
    firstItemOffset: Int = 0,
): CRUDMutablePager<Value> = CRUDMutablePager(
    sort,
    predicate,
    create,
    properties,
    getValues,
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) { sort, predicate -> findPagingSource(sort, predicate, firstItemOffset) }

@OptIn(ExperimentalPagingApi::class)
public fun CRUDRepository<*>.findPagingSource(
    projections: List<Variable>,
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    firstItemOffset: Int = 0,
): PagingSource<Int, List<Any?>> = CRUDPagingSource({ find(projections, sort, predicate, it).toList() }, firstItemOffset)

@OptIn(ExperimentalPagingApi::class)
public fun CRUDRepository<*>.pager(
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

@OptIn(ExperimentalPagingApi::class)
public fun CRUDRepository<*>.mutablePager(
    projections: List<Variable>,
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    create: (id: Any) -> List<Any?>,
    properties: List<EntityColumn>,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, List<Any?>>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
    firstItemOffset: Int = 0,
): CRUDProjectionsMutablePager = CRUDProjectionsMutablePager(
    projections,
    sort,
    predicate,
    create,
    properties,
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) { projections, sort, predicate -> findPagingSource(projections, sort, predicate, firstItemOffset) }
