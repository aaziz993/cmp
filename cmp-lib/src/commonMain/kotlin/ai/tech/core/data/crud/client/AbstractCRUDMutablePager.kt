package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.model.Modification
import ai.tech.core.data.crud.client.model.EntityItem
import ai.tech.core.data.crud.client.model.isSelectedAll
import ai.tech.core.data.crud.client.model.mutations
import ai.tech.core.data.crud.client.model.news
import ai.tech.core.data.crud.client.model.selected
import ai.tech.core.data.crud.client.model.selectedExists
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.f
import ai.tech.core.data.paging.AbstractMutablePager
import ai.tech.core.misc.type.letIf
import ai.tech.core.misc.type.multiple.replaceAt
import ai.tech.core.misc.type.multiple.replaceIfFirst
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.RemoteMediator
import app.cash.paging.insertFooterItem
import app.cash.paging.map
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.update

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
