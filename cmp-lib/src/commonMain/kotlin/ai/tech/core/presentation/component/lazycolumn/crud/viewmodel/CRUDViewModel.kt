package ai.tech.core.presentation.component.lazycolumn.crud.viewmodel

import ai.tech.core.data.database.crud.PagingCRUDRepository
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.f
import ai.tech.core.misc.type.kClass
import ai.tech.core.misc.type.model.Property
import ai.tech.core.misc.type.parsePrimeOrNull
import ai.tech.core.misc.type.primeTypeOrNull
import ai.tech.core.presentation.component.lazycolumn.crud.model.Item
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState
import ai.tech.core.presentation.viewmodel.AbstractViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import app.cash.paging.filter
import app.cash.paging.insertFooterItem
import app.cash.paging.map
import kotlin.collections.filter
import kotlin.collections.map
import kotlin.reflect.typeOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

public class CRUDViewModel<T : Any>(
    private val repository: PagingCRUDRepository<T>,
    private val properties: List<Property>,
    private val getEntityId: (T) -> Any,
    private val getEntityValues: (T) -> List<Any?>,
    private val newEntity: () -> T,
    savedStateHandle: SavedStateHandle
) : AbstractViewModel<CRUDAction<T>>(savedStateHandle) {

    private val items = MutableStateFlow<List<Item<T>>>(emptyList())
    private val removedItems = MutableStateFlow<List<Any>>(emptyList())
    private val appendedItems = MutableStateFlow<List<Item<T>>>(emptyList())

    override fun action(action: CRUDAction<T>) {
        when (action) {
            is CRUDAction.Find -> {
                combine(repository.viewModelPagingDataFlow(action.sort, action.searchFieldStates.predicate(), action.limitOffset), items, removedItems, appendedItems) { pagingData, items, removed, appended ->
                    val modifiedPagingData = pagingData.map(::entityToItem).filter { it.id !in removed }.map { pagingItem ->
                        items.findLast { it.id == pagingItem.id } ?: pagingItem
                    }

                    appended.fold(modifiedPagingData) { acc, v -> acc.insertFooterItem(item = v) }
                }.launchIn(viewModelScope)
            }

            is CRUDAction.Delete -> {
                viewModelScope.launch {
                    items.value.filter { it.id in action.ids }.map { "id".f eq it.id }.forEach {
                        repository.delete(it)
                    }
                }
            }

            is CRUDAction.Append -> appendedItems.update { it + entityToItem(newEntity()) }

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

            is CRUDAction.Select -> {
                items.update { it.select(action.ids) }
                appendedItems.update { it.select(action.ids) }
            }

            CRUDAction.SelectAll -> {
                items.update { it.selectAll() }
                appendedItems.update { it.selectAll() }
            }
        }
    }

    private fun entityToItem(entity: T): Item<T> = Item(entity, getEntityId(entity), getEntityValues(entity))

    private fun List<SearchFieldState>.predicate(): BooleanVariable? =
        withIndex().filter { (_, value) -> value.query.isNotEmpty() }
            .map { (index, value) -> properties[index].predicate(value) }.filterNotNull().reduce { acc, v -> acc.and(v) }

    private fun Property.predicate(state: SearchFieldState): BooleanVariable? = when {
            descriptor.primeTypeOrNull == typeOf<String>() -> {
                if (state.compareMatch == -3) {
                    name.f.neq(state.query)
                } else if (state.regexMatch) {
                    name.f.eqPattern(
                        state.query,
                        state.wordMatch,
                        !state.caseMatch
                    )
                } else {
                    name.f.eq(
                        state.query,
                        state.wordMatch,
                        !state.caseMatch
                    )
                }
            }

            else -> {
                if (state.compareMatch == 3) {
                    val left = state.query.substringBefore("..").ifEmpty { null }
                        ?.let { descriptor.primeTypeOrNull!!.kClass.parsePrimeOrNull(it) }
                    val right = state.query.substringAfter("..").ifEmpty { null }
                        ?.let { descriptor.primeTypeOrNull!!.kClass.parsePrimeOrNull(it) }
                    if (!(left == null || right == null)) {
                        name.f.between(left, right)
                    } else {
                        null
                    }
                } else {
                    descriptor.primeTypeOrNull!!.kClass.parsePrimeOrNull(state.query)?.let {
                        when (state.compareMatch) {
                            2 -> name.f.gt(it)
                            1 -> name.f.gte(it)
                            0 -> name.f.eq(it)
                            -2 -> name.f.lt(it)
                            -1 -> name.f.lte(it)
                            -3 -> name.f.neq(it)
                            else -> throw IllegalStateException()
                        }
                    }
                }
            }
        }

    private fun List<Item<T>>.select(ids: List<Any>): List<Item<T>> = filter { it.id in ids }.map { it.copy(select = !it.select) }.filter {  }

    private fun List<Item<T>>.selectAll(): List<Item<T>> = map { it.copy(select = !it.select) }
}
