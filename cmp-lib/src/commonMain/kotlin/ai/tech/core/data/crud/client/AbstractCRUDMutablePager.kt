package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.model.Modification
import ai.tech.core.data.crud.client.model.MutationItem
import ai.tech.core.data.crud.client.model.actuals
import ai.tech.core.data.crud.client.model.isSelectedAll
import ai.tech.core.data.crud.client.model.news
import ai.tech.core.data.crud.client.model.selected
import ai.tech.core.data.crud.client.model.selectedExists
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

    public val idIndex: Int = properties.indexOfFirst(EntityProperty::isId)

    public val idName: String = properties[idIndex].name

    public val selectedNewEntities: List<Value>
        get() = mutations.value.selected.news.map(::toEntity)

    public val selectedEditEntities: List<Value>
        get() = mutations.value.selected.news.map(::toEntity)

    public val selectedIdPredicate: BooleanVariable?
        get() = mutations.value.selectedExists.ifEmpty { null }?.map { idName.f.eq(it.id) as BooleanVariable }?.reduce { acc, v -> acc.and(v) }

    final override fun mergeMutations(pagingData: PagingData<Value>, mutations: List<MutationItem<Value>>): PagingData<MutationItem<Value>> {
        val (insertMutations, mergeMutations) = mutations.partition(MutationItem<Value>::isNew)

        val mergedPagingData = pagingData
            .map {
                val values = getValues(it)
                MutationItem(it, values[idIndex]!!, values)
            }
            .map { pagingItem -> mergeMutations.find { it.id == pagingItem.id } ?: pagingItem }

        return insertMutations.fold(mergedPagingData) { acc, v -> acc.insertFooterItem(item = v) }
    }

    public final override fun refresh(): Unit = super.refresh()

    protected abstract fun createEntity(): Value

    protected abstract fun toEntity(item: MutationItem<Value>): Value

    public fun new(): Unit = mutations.update { it + createEntity().let { MutationItem(it, uuid4(), getValues(it), Modification.NEW) } }

    public fun newFrom(item: MutationItem<Value>): Unit = mutations.update { it + item.copy(modification = Modification.NEW) }

    public fun newFromSelected(): Unit = mutations.update { it + it.selected.map { it.copy(modification = Modification.NEW) } }

    public fun removeSelectedNews(): Unit = mutations.update { it.filterNot(MutationItem<Value>::isSelectedNew) }

    public fun edit(item: MutationItem<Value>): Unit = mutations.update { it + item.copy(modification = Modification.EDIT) }

    public fun editSelected(): Unit = mutations.update {
        val (edits, others) = it.partition(MutationItem<Value>::isEdit)

        others + if (edits.isSelectedAll) {
            edits.map { it.copy(values = getValues(it.entity), modification = null) }
        }
        else {
            edits.map { it.copy(modification = Modification.EDIT) }
        }.actuals
    }

    public fun setValue(id: Any, index: Int, value: String) {}

    public fun select(items: List<MutationItem<Value>>): Unit = mutations.update { it + items.map { it.copy(isSelected = true) } }

    public fun unselectAll(): Unit =
        mutations.update { it.map { it.copy(isSelected = false) }.actuals }

    public fun remove(ids: List<Any>): Unit = mutations.update { it.filterNot { it.id in ids } }
}
