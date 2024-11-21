package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.model.MutationItem
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
    getValues: (Value) -> List<Any?>,
    private val create: (Map<String, Any?>) -> Value,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, Value>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
    private val pagingSourceFactory: (sort: List<Order>?, predicate: BooleanVariable?) -> PagingSource<Int, Value>,
) : AbstractCRUDMutablePager<Value>(
    properties,
    getValues,
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) {
    override fun createPagingSource(): PagingSource<Int, Value> = pagingSourceFactory(sort, predicate)

    public override fun refresh(): Unit = pagingSource.invalidate()

    override fun createEntity(): Value = create(emptyMap())

    override fun toEntity(item: MutationItem<Value>): Value = create(properties.map(EntityProperty::name).zip(item.values).toMap())

    public fun load(
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
    ) {
        this.sort = sort
        this.predicate = predicate
        refresh()
    }
}
