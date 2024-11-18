package ai.tech.core.presentation.component.lazycolumn.crud.viewmodel

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.f
import ai.tech.core.presentation.component.lazycolumn.crud.model.EntityColumn
import ai.tech.core.presentation.component.lazycolumn.crud.model.Item
import ai.tech.core.presentation.component.lazycolumn.crud.model.isSelectedAll
import ai.tech.core.presentation.component.lazycolumn.crud.model.isSelectedAllExistsIsEditing
import ai.tech.core.presentation.component.lazycolumn.crud.model.selected
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState
import ai.tech.core.presentation.viewmodel.AbstractViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import app.cash.paging.RemoteMediator
import app.cash.paging.createPagingConfig
import app.cash.paging.insertFooterItem
import app.cash.paging.map
import com.benasher44.uuid.uuid4
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagingApi::class)
public class CRUDViewModel<T : Any> (
    private val repository: CRUDRepository<T>,
    public val config: PagingConfig = createPagingConfig(10),
    public val initialKey: Int? = null,
    private val remoteMediator: RemoteMediator<Int, T>? = null,
    public val firstItemOffset: Int = 0,
    public val properties: List<EntityColumn>,
    private val getValues: (T) -> List<Any?>,
    private val getNew: (id: Any) -> T,
    savedStateHandle: SavedStateHandle
) : AbstractViewModel<CRUDAction<T>>(savedStateHandle) {

    private val idIndex = properties.indexOfFirst(EntityColumn::isId)

    private val idName = properties[idIndex].name

    public val state: MutableStateFlow<PagingData<Item<T>>>
        field = MutableStateFlow()

    public val items: StateFlow<List<Item<T>>>
        field = MutableStateFlow<List<Item<T>>>(emptyList())

    @OptIn(ExperimentalPagingApi::class)
    override fun action(action: CRUDAction<T>) {
        when (action) {
            is CRUDAction.Find -> {
                combine(repository.viewModelPagingFlow(action.sort, action.searchFieldStates.predicate(), config, initialKey, remoteMediator, firstItemOffset), items) { pagingData, items ->
                    val modifiedPagingData = pagingData.map(::newItem).map { pagingItem ->
                        items.find { !it.isNew && it.id == pagingItem.id } ?: pagingItem
                    }

                    items.filter(Item<T>::isNew).fold(modifiedPagingData) { acc, v -> acc.insertFooterItem(item = v) }
                }.onEach { pagingData -> state.update { pagingData } }.launchIn(viewModelScope)
            }

            is CRUDAction.Add -> items.update { it + newItem(getNew(uuid4())) }

            is CRUDAction.Copy -> {
//                state.update { data -> data  }
            }

            CRUDAction.CopySelected -> items.update { it + it.selected.map(::copyItem) }

            is CRUDAction.Remove -> items.update { it.filterNot { it.id == action.id } }

            CRUDAction.RemoveSelected -> items.update { it.filterNot { it.isSelected && it.isNew } }

            is CRUDAction.Edit -> items.update { it.edit(action.id) }

            is CRUDAction.EditSelected -> items.update { it.editSelected() }

            is CRUDAction.ChangeValue -> items.update { it.editSelected() }

            is CRUDAction.Upload -> {
                viewModelScope.launch {
                    repository.insert(action.entities)
                }
            }

            is CRUDAction.Save -> viewModelScope.launch {

            }

            is CRUDAction.SaveSelected -> viewModelScope.launch {

            }

            is CRUDAction.Delete -> viewModelScope.launch {
                items.update {
                    it - it.single { it.id == action.id }.also {
                        delete(listOf(it.id))
                    }
                }
            }

            CRUDAction.DeleteSelected -> viewModelScope.launch {
                items.update {
                    it - it.filter { it.isSelected && !it.isNew }.also {
                        delete(it.map(Item<T>::id))
                    }
                }
            }

            is CRUDAction.Select -> items.update { it.select(action.id) }

            CRUDAction.SelectAll -> items.update { it.selectAll() }
        }
    }

    private fun List<SearchFieldState>.predicate(): BooleanVariable? =
        mapIndexed { index, state ->
            if (state.query.isEmpty()) {
                return null
            }

            properties[index].predicate(state)
        }.filterNotNull().reduce { acc, v -> acc.and(v) }

    private fun newItem(entity: T): Item<T> =
        getValues(entity).let { values -> Item(entity, values[idIndex]!!, values) }

    private fun copyItem(item: Item<T>) = item.copy(isNew = true, isEditing = true)

    private fun List<Item<T>>.edit(id: Any): List<Item<T>> {
        val item = single { it.id == id }

        val result = this - item

        if (item.isEditing) {
            if (!(item.isSelected || item.isNew)) {
                return result
            }

            return result + item.copy(values = getValues(item.entity), isEditing = false)
        }

        return result + item.copy(isEditing = true)
    }

    public fun List<Item<T>>.editSelected(): List<Item<T>> {
        if (isSelectedAllExistsIsEditing) {

        }
        else {

        }
    }

    private suspend fun delete(ids: List<Any>) = repository.delete(ids.map { (properties[idIndex].name.f eq it) as BooleanVariable }.reduce { acc, v -> acc.or(v) })

    private fun List<Item<T>>.select(id: Any): List<Item<T>> {
        val item = single { it.id == id }

        val result = this - item

        if (item.isSelected) {
            if (!(item.isEditing || item.isNew)) {
                return result
            }

            return result + item.copy(isSelected = false)
        }

        return result + item.copy(isSelected = true)
    }

    private fun List<Item<T>>.selectAll(): List<Item<T>> {
        val isSelectedAll = isSelectedAll
        return map { it.copy(isSelected = !isSelectedAll) }.filter(Item<T>::isNecessary)
    }
}
