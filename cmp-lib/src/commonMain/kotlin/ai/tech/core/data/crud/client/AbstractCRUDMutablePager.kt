package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.model.Modification
import ai.tech.core.data.crud.client.model.MutationItem
import ai.tech.core.data.crud.client.model.isSelectedAll
import ai.tech.core.data.crud.client.model.selected
import ai.tech.core.data.crud.client.model.selectedExists
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.f
import ai.tech.core.data.paging.AbstractMutablePager
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
    protected var sort: List<Order>? = null,
    protected var predicate: BooleanVariable? = null,
    private val create: () -> Value,
    public val properties: List<EntityProperty>,
    private val getValues: (Value) -> List<Any?>,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, Value>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
) : AbstractMutablePager<Int, Value, MutationItem<Value>>(
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

    public fun add(): Unit = mutations.update { it + create().let { MutationItem(it, uuid4(), getValues(it), Modification.NEW) } }

    public fun copySelected(): Unit = mutations.update { it + it.selected.map { it.copy(modification = Modification.NEW) } }

    public fun removeSelected(): Unit = mutations.update { it.filterNot(MutationItem<Value>::isSelectedNew) }

    public fun editSelected(): Unit = mutations.update {
        val (edits, others) = it.partition(MutationItem<Value>::isEdit)

        if (edits.isSelectedAll) {
            edits.map { it.copy(values = getValues(it.entity), modification = null) }
        }
        else {
            edits.map { it.copy(modification = Modification.EDIT) }
        }.filter(MutationItem<Value>::isActual) + others
    }

    public fun getSelectedModifies(): List<Value> = mutations.selectedModifies

    public fun getSelectedIdPredicate(): BooleanVariable? =
        mutations.value.selectedExists.ifEmpty { null }?.map { idName.f.eq(it.id) as BooleanVariable }?.reduce { acc, v -> acc.and(v) }

    public fun deleteSelected(): Unit =
        mutations.update { it.filterNot(MutationItem<Value>::isSelectedExist) }
}
