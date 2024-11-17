package ai.tech.core.presentation.component.lazycolumn.crud.viewmodel

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.client.findPager
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.f
import ai.tech.core.presentation.component.lazycolumn.crud.model.EntityColumn
import ai.tech.core.presentation.component.lazycolumn.crud.model.Item
import ai.tech.core.presentation.component.lazycolumn.crud.model.selected
import ai.tech.core.presentation.component.lazycolumn.crud.model.selectedNotNew
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState
import ai.tech.core.presentation.viewmodel.AbstractViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingData
import app.cash.paging.insertFooterItem
import app.cash.paging.map
import com.benasher44.uuid.uuid4
import kotlin.collections.filter
import kotlin.collections.filterNot
import kotlin.collections.map
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

public class CRUDViewModel<T : Any>(
    private val repository: CRUDRepository<T>,
    public val properties: List<EntityColumn>,
    private val getValues: (T) -> List<Any?>,
    private val getNew: (id: Any) -> T,
    savedStateHandle: SavedStateHandle
) : AbstractViewModel<CRUDAction>(savedStateHandle) {

    private val idIndex = properties.indexOfFirst(EntityColumn::isId)

    public val state: MutableStateFlow<PagingData<Item<T>>>
        field = MutableStateFlow()

    public val items: MutableStateFlow<List<Item<T>>>
        field = MutableStateFlow<List<Item<T>>>(emptyList())

    @OptIn(ExperimentalPagingApi::class)
    override fun action(action: CRUDAction) {
        when (action) {
            is CRUDAction.Find -> {
                combine(repository.findPager(action.sort, action.searchFieldStates.predicate(), action.limitOffset).flow.cached, items) { pagingData, items ->
                    val modifiedPagingData = pagingData.map(::toItem).map { pagingItem ->
                        items.findLast { !it.isNew && it.id == pagingItem.id } ?: pagingItem
                    }

                    items.filter(Item<T>::isNew).fold(modifiedPagingData) { acc, v -> acc.insertFooterItem(item = v) }
                }.onEach { pagingData -> state.update { pagingData } }.launchIn(viewModelScope)
            }

            is CRUDAction.New -> items.update { it + toItem(getNew(uuid4())) }

            is CRUDAction.Copy -> items.update { it + it.single { it.id == action.id }.let(::copy) }

            CRUDAction.CopySelected -> items.update { it + it.selected.map(::copy) }

            is CRUDAction.Edit -> items.update { it.edit(listOf(action.id)) }

            is CRUDAction.EditSelected -> items.update { it.edit(it.selectedNotNew.map(Item<T>::id)) }

            is CRUDAction.Delete -> viewModelScope.launch {
                items.update {
                    delete(listOf(action.id))

                    it.filterNot { it.id == action.id }
                }
            }

            CRUDAction.DeleteSelected -> viewModelScope.launch {
                items.update {
                    delete(it.filter { it.isSelected && !it.isNew }.map(Item<T>::id))

                    it.filterNot(Item<T>::isSelected)
                }
            }

            is CRUDAction.Select -> items.update { it.select(action.id) }

            CRUDAction.SelectAll -> items.update { it.selectAll() }
        }
    }

    private fun toItem(entity: T): Item<T> =
        getValues(entity).let { values ->
            Item(entity, values[idIndex]!!, values)
        }

    private fun List<SearchFieldState>.predicate(): BooleanVariable? =
        withIndex().filter { (_, value) -> value.query.isNotEmpty() }
            .map { (index, value) -> properties[index].predicate(value) }.filterNotNull().reduce { acc, v -> acc.and(v) }

    private fun copy(item: Item<T>) = item.copy(values = getValues(item.entity), isNew = true, isEditing = true)

    private fun List<Item<T>>.edit(ids: List<Any>) = map {
        if (it.id in ids) {
            if (it.isEditing) {
                return@map it.copy(values = getValues(it.entity), isEditing = false)
            }

            return@map it.copy(isEditing = true)
        }
        it
    }.filter(Item<T>::isModifying)

    private suspend fun delete(ids: List<Any>) = repository.delete(ids.map { (properties[idIndex].name.f eq it) as BooleanVariable }.reduce { acc, v -> acc.or(v) })

    private fun List<Item<T>>.select(id: Any) = map {
        if (it.id == id) {
            return@map it.copy(isSelected = !it.isSelected)
        }
        it
    }.filter(Item<T>::isModifying)

    private fun List<Item<T>>.selectAll() = map { it.copy(isSelected = !it.isSelected) }.filter(Item<T>::isModifying)
}
