package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.model.EntityItem
import ai.tech.core.data.paging.AbstractMutablePager
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.RemoteMediator
import app.cash.paging.insertFooterItem
import app.cash.paging.map
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalPagingApi::class)
public abstract class AbstractCRUDMutablePager<Value : Any>(
    public val properties: List<EntityProperty>,
    protected val getEntityValues: (Value) -> List<String>,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, Value>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
) : AbstractMutablePager<Int, Value, EntityItem<Value>>(
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) {
    public val idIndex: Int = properties.indexOfFirst(EntityProperty::isId)

    public val idName: String = properties[idIndex].name

    final override fun mergeMutations(pagingData: PagingData<Value>, mutations: List<EntityItem<Value>>): PagingData<EntityItem<Value>> {
        val (insertMutations, mergeMutations) = mutations.partition(EntityItem<Value>::isNew)

        val mergedPagingData = pagingData
            .map(::createItem)
            .map { pagingItem -> mergeMutations.find { it.id == pagingItem.id } ?: pagingItem }

        return insertMutations.fold(mergedPagingData) { acc, v -> acc.insertFooterItem(item = v) }
    }

    private fun createItem(entity: Value): EntityItem<Value> = getEntityValues(entity).let { values ->
        EntityItem(entity, values[idIndex], values)
    }
}
