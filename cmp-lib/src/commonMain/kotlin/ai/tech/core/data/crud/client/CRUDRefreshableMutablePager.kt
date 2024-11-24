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
    private var sort: List<Order>? = null,
    private var predicate: BooleanVariable? = null,
    properties: List<EntityProperty>,
    getEntityValues: (Value) -> List<String>,
    private val createEntity: (Map<String, String>) -> Value,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, Value>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
    private val pagingSourceFactory: (sort: List<Order>?, predicate: BooleanVariable?) -> PagingSource<Int, Value>,
) : AbstractCRUDMutablePager<Value>(
    properties,
    getEntityValues,
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) {

    override fun createPagingSource(): PagingSource<Int, Value> = pagingSourceFactory(sort, predicate)

    override fun createEntity(): Value = createEntity(emptyMap())

    override fun createEntity(values: List<String>): Value = createEntity(properties.map(EntityProperty::name).zip(values).toMap())

    public fun find(
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
    ) {
        this.sort = sort
        this.predicate = predicate
        refresh()
    }
}
