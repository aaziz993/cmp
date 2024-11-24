package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.model.Modification
import ai.tech.core.data.crud.client.model.MutationItem
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
        get() = mutations.value.selected.news.map(::createFrom)

    public val selectedEditEntities: List<Value>
        get() = mutations.value.selected.news.map(::createFrom)

    public val selectedIdPredicate: BooleanVariable?
        get() = mutations.value.selectedExists.ifEmpty { null }?.map { idPredicate(it.id) }?.reduce { acc, v -> acc.and(v) }

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

    protected abstract fun create(): Value

    protected abstract fun create(values: List<Any?>): Value

    public fun createFrom(item: MutationItem<Value>): Value = create(item.values)

    public fun idPredicate(id: Any): BooleanVariable = idName.f eq id

    public fun new(): Unit = mutations.update { it + create().let { MutationItem(it, uuid4(), getValues(it), Modification.NEW) } }

    public fun newFrom(item: MutationItem<Value>): Unit = mutations.update { it + item.copy(modification = Modification.NEW) }

    public fun newFromSelected(): Unit = mutations.update { it + it.selected.map { it.copy(modification = Modification.NEW) } }

    public fun removeSelectedNews(): Unit = mutations.update { it.filterNot(MutationItem<Value>::isSelectedNew) }

    public fun editSelected(): Unit = mutations.update {
        val (edits, others) = it.partition(MutationItem<Value>::isEdit)

        others + if (edits.isSelectedAll) {
            edits.map { it.copy(values = getValues(it.entity), modification = null) }
        }
        else {
            edits.map { it.copy(modification = Modification.EDIT) }
        }.mutations
    }

    public fun selectOrUnselect(item: MutationItem<Value>): Unit = mutate(item) { copy(isSelected = !isSelected) }

    public fun editOrUnEdit(item: MutationItem<Value>): Unit = mutate(item) {
        copy(
            modification = if (modification == null) {
                Modification.EDIT
            }
            else {
                null
            },
        )
    }

    public fun setValue(id: Any, index: Int, value: String): Unit = mutations.update {
        it.replaceIfFirst({ it.id == id }) {
            copy(values = values.replaceAt(index) { value }.toList())
        }.toList()
    }

    public fun selectAll(items: List<MutationItem<Value>>): Unit = mutations.update { it + items.map { it.copy(isSelected = true) } }

    public fun unselectAll(): Unit =
        mutations.update { it.map { it.copy(isSelected = false) }.mutations }

    public fun remove(id: Any): Unit = mutations.update { it.filterNot { it.id == id } }

    private fun mutate(item: MutationItem<Value>, block: MutationItem<Value>.() -> MutationItem<Value>) = mutations.update { items ->
        val mutated = item.block()
        if (items.any { it.id == item.id }) {
            (items - item).letIf({ mutated.isMutated }) { items + it }
        }
        else {
            items + mutated
        }
    }
}
