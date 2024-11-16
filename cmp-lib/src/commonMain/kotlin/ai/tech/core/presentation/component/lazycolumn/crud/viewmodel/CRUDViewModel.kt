package ai.tech.core.presentation.component.lazycolumn.crud.viewmodel

import ai.tech.core.data.database.crud.PagingCRUDRepository
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.f
import ai.tech.core.presentation.component.lazycolumn.crud.model.EntityProperty
import ai.tech.core.presentation.component.lazycolumn.crud.model.Item
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState
import ai.tech.core.presentation.viewmodel.AbstractViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import app.cash.paging.PagingData
import app.cash.paging.compose.LazyPagingItems
import app.cash.paging.filter
import app.cash.paging.insertFooterItem
import app.cash.paging.map
import kotlin.collections.filter
import kotlin.collections.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

public class CRUDViewModel<T : Any>(
    private val repository: PagingCRUDRepository<T>,
    private val properties: List<EntityProperty>,
    private val getEntityId: (T) -> Any,
    private val getEntityValues: (T) -> List<Any?>,
    private val newEntity: () -> T,
    savedStateHandle: SavedStateHandle
) : AbstractViewModel<CRUDAction<T>>(savedStateHandle) {

    public val state: MutableStateFlow<PagingData<Item<T>>>
        field = MutableStateFlow()

    private val items = MutableStateFlow<List<Item<T>>>(emptyList())

    override fun action(action: CRUDAction<T>) {
        when (action) {
            is CRUDAction.Find -> {
                combine(repository.viewModelPagingDataFlow(action.sort, action.searchFieldStates.predicate(), action.limitOffset), items) { pagingData, items ->
                    val modifiedPagingData = pagingData.map(::entityToItem).map { pagingItem ->
                        items.findLast { !it.append && it.id == pagingItem.id } ?: pagingItem
                    }
                    items.filter(Item<*>::append).fold(modifiedPagingData) { acc, v -> acc.insertFooterItem(item = v) }
                }.onEach { pagingData -> state.update { pagingData } }.launchIn(viewModelScope)
            }

            is CRUDAction.Delete -> {
                viewModelScope.launch {
                    items.value.filter { it.id in action.ids }.map { "id".f eq it.id }.forEach {
                        repository.delete(it)
                    }
                }
            }

            is CRUDAction.Append -> items.update { it + entityToItem(newEntity()) }

            is CRUDAction.Edit -> items.update {
                it.map {
                    if (it.id in action.ids) {
                        if (it.edit) {
                            return@map it.copy(values = getEntityValues(it.entity), edit = false)
                        }

                        return@map it.copy(edit = true)
                    }
                    it
                }
            }

            is CRUDAction.Copy -> appendedItems.update {
                it + items.value.filter { it.id in action.ids }.map { it.copy(values = getEntityValues(it.entity), edit = true) }
            }

            is CRUDAction.Select -> items.update { it.select(action.ids) }

            CRUDAction.SelectAll -> items.update { it.selectAll() }
        }
    }

    private fun entityToItem(entity: T): Item<T> = Item(entity, getEntityId(entity), getEntityValues(entity))

    private fun List<SearchFieldState>.predicate(): BooleanVariable? =
        withIndex().filter { (_, value) -> value.query.isNotEmpty() }
            .map { (index, value) -> properties[index].predicate(value) }.filterNotNull().reduce { acc, v -> acc.and(v) }

    private fun List<Item<T>>.select(ids: List<Any>): List<Item<T>> = filter { it.id in ids }.map { it.copy(select = !it.select) }.filter { }

    private fun List<Item<T>>.selectAll(): List<Item<T>> = map { it.copy(select = !it.select) }
}
