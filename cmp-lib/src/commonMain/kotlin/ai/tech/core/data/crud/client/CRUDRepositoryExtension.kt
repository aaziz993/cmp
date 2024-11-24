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
    properties: List<EntityProperty>,
    getEntityValues: (Value) -> List<String>,
    createEntity: (Map<String, String>) -> Value,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, Value>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
    firstItemOffset: Int = 0,
    disablePrepend: Boolean = false,
): CRUDRefreshableMutablePager<Value> = CRUDRefreshableMutablePager(
    sort,
    predicate,
    properties,
    getEntityValues,
    createEntity,
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) { sort, predicate -> pagingSource(sort, predicate, firstItemOffset, disablePrepend) }
