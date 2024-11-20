package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.expression.BooleanVariable
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.RemoteMediator
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalPagingApi::class)
public class CRUDRefreshableMutablePager<Value : Any>(
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    create: (id: Any) -> Value,
    properties: List<EntityProperty>,
    getValues: (Value) -> List<Any?>,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, Value>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
    private val pagingSourceFactory: (sort: List<Order>?, predicate: BooleanVariable?) -> PagingSource<Int, Value>,
) : AbstractCRUDMutablePager<Value>(
    sort,
    predicate,
    create,
    properties,
    getValues,
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) {

    override fun createPagingSource(): PagingSource<Int, Value> = pagingSourceFactory(sort, predicate)


    public fun refresh(): Unit = pagingSource.invalidate()

    public fun refresh(
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
    ) {
        this.sort = sort
        this.predicate = predicate
        refresh()
    }
}
