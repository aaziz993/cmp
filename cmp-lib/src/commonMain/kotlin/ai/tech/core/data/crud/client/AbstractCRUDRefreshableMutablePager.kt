package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.model.MutationItem
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.paging.AbstractRefreshableMutablePager
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.RemoteMediator
import app.cash.paging.insertFooterItem
import app.cash.paging.map
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalPagingApi::class)
public abstract class AbstractCRUDRefreshableMutablePager<Value : Any>(
    protected var sort: List<Order>? = null,
    protected var predicate: BooleanVariable? = null,
    private val create: (id: Any) -> Value,
    public val properties: List<EntityProperty>,
    private val getValues: (Value) -> List<Any?>,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, Value>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
) : AbstractRefreshableMutablePager<Int, Value, MutationItem<Value>>(
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) {

    private val idIndex = properties.indexOfFirst(EntityProperty::isId)

    private val idName = properties[idIndex].name

    override fun mergeMutations(pagingData: PagingData<Value>, mutations: List<MutationItem<Value>>): PagingData<MutationItem<Value>> {
        val (newMutations, mergeMutations) = mutations.partition(MutationItem<Value>::isNew)

        val mergedPagingData = pagingData.map {
            val values = getValues(it)
            MutationItem(it, values[idIndex]!!, values)
        }.map { pagingItem -> mergeMutations.find { it.id == pagingItem.id } ?: pagingItem }

        return newMutations.fold(mergedPagingData) { acc, v -> acc.insertFooterItem(item = v) }
    }
}
