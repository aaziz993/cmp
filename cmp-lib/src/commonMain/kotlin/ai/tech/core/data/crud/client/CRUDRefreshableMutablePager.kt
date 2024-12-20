package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.client.model.EntityItem
import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.model.Modification
import ai.tech.core.data.crud.client.model.isSelectedAll
import ai.tech.core.data.crud.client.model.mutations
import ai.tech.core.data.crud.client.model.news
import ai.tech.core.data.crud.client.model.selected
import ai.tech.core.data.crud.client.model.selectedExists
import ai.tech.core.data.crud.model.query.Order
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.f
import ai.tech.core.misc.type.letIf
import ai.tech.core.misc.type.multiple.iterable.replaceAt
import ai.tech.core.misc.type.multiple.iterable.replaceIfFirst
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.RemoteMediator
import com.benasher44.uuid.uuid4
import kotlin.collections.ifEmpty
import kotlin.collections.map
import kotlin.collections.reduce
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.update

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
    public val selectedNewEntities: List<Value>
        get() = mutations.value.selected.news.map(::createEntity)

    public val selectedEditEntities: List<Value>
        get() = mutations.value.selected.news.map(::createEntity)

    public val selectedIdPredicate: BooleanVariable?
        get() = mutations.value.selectedExists.ifEmpty { null }?.map { idPredicate(it.id) }?.reduce { acc, v -> acc.and(v) }

    override fun createPagingSource(): PagingSource<Int, Value> = pagingSourceFactory(sort, predicate)

    private fun createEntity(): Value = createEntity(emptyMap())

    private fun createEntity(values: List<String>): Value = createEntity(properties.map(EntityProperty::name).zip(values).toMap())

    public fun createEntity(item: EntityItem<Value>): Value = createEntity(item.values)

    public fun idPredicate(id: Any): BooleanVariable = idName.f eq id

    public fun new(): Unit = mutations.update { it + createEntity().let { EntityItem(it, uuid4(), getEntityValues(it), Modification.NEW) } }

    public fun newFrom(item: EntityItem<Value>): Unit = mutations.update { it + item.copy(modification = Modification.NEW) }

    public fun selectOrUnselect(item: EntityItem<Value>): Unit = mutate(item) { copy(isSelected = !isSelected) }

    public fun editOrUnEdit(item: EntityItem<Value>): Unit = mutate(item) {
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

    public fun remove(id: Any): Unit = mutations.update { it.filterNot { it.id == id } }

    public fun newFromSelected(): Unit = mutations.update { it + it.selected.map { it.copy(modification = Modification.NEW) } }

    public fun removeSelectedNews(): Unit = mutations.update { it.filterNot(EntityItem<Value>::isSelectedNew) }

    public fun editSelected(): Unit = mutations.update {
        val (edits, others) = it.partition(EntityItem<Value>::isEdit)

        others + if (edits.isSelectedAll) {
            edits.map { it.copy(values = getEntityValues(it.entity), modification = null) }
        }
        else {
            edits.map { it.copy(modification = Modification.EDIT) }
        }.mutations
    }

    public fun selectAll(items: List<EntityItem<Value>>): Unit = mutations.update { it + items.map { it.copy(isSelected = true) } }

    public fun unselectAll(): Unit =
        mutations.update { it.map { it.copy(isSelected = false) }.mutations }

    public override fun refresh(): Unit = super.refresh()

    public fun find(
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
    ) {
        this.sort = sort
        this.predicate = predicate
        refresh()
    }

    private fun mutate(item: EntityItem<Value>, block: EntityItem<Value>.() -> EntityItem<Value>) = mutations.update { items ->
        val mutated = item.block()
        if (items.any { it.id == item.id }) {
            (items - item).letIf({ mutated.isMutated }) { items + it }
        }
        else {
            items + mutated
        }
    }
}
