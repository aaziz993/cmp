package ai.tech.core.presentation.component.lazycolumn.crud.viewmodel

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.client.AbstractCRUDMutablePager
import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.model.predicate
import ai.tech.core.data.crud.model.query.Order
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.presentation.viewmodel.AbstractViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.RemoteMediator
import app.cash.paging.createPagingConfig
import kotlinx.coroutines.launch

@OptIn(ExperimentalPagingApi::class)
public class CRUDViewModel<T : Any>(
    private val repository: CRUDRepository<T>,
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    properties: List<EntityProperty>,
    getEntityValues: (T) -> List<String>,
    createEntity: (Map<String, String>) -> T,
    config: PagingConfig = createPagingConfig(10),
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, T>? = null,
    firstItemOffset: Int = 0,
    savedStateHandle: SavedStateHandle
) : AbstractViewModel<CRUDAction<T>>(savedStateHandle) {

    public val pager: AbstractCRUDMutablePager<T>
        field = repository.viewModelMutablePager(
            sort,
            predicate,
            properties,
            getEntityValues,
            createEntity,
            config,
            initialKey,
            remoteMediator,
            firstItemOffset,
        )

    @OptIn(ExperimentalPagingApi::class)
    override fun action(action: CRUDAction<T>) {
        when (action) {
            is CRUDAction.Find -> pager.load(action.sort, pager.properties.predicate(action.searchFieldStates))

            CRUDAction.New -> pager.new()

            is CRUDAction.NewFrom<T> -> pager.newFrom(action.item)

            is CRUDAction.EditOrUnEdit -> pager.editOrUnEdit(action.item)

            is CRUDAction.SelectOrUnselect -> pager.selectOrUnselect(action.item)

            is CRUDAction.SetValue -> pager.setValue(action.id, action.index, action.value)

            is CRUDAction.SelectAll -> pager.selectAll(action.items)

            is CRUDAction.Remove -> pager.remove(action.id)

            CRUDAction.UnselectAll -> pager.unselectAll()

            CRUDAction.NewFromSelected -> pager.newFromSelected()

            CRUDAction.RemoveSelected -> pager.removeSelectedNews()

            CRUDAction.EditSelected -> pager.editSelected()

            CRUDAction.SaveSelected -> viewModelScope.launch {
                repository.insert(pager.selectedNewEntities)
                repository.update(pager.selectedNewEntities)
            }

            CRUDAction.DeleteSelected -> viewModelScope.launch {
                pager.selectedIdPredicate?.let { predicate -> repository.delete(predicate) }
            }

            is CRUDAction.Save -> viewModelScope.launch {
                val entity = pager.createEntity(action.item)

                if (action.item.isNew) {
                    repository.insert(entity)
                }
                else {
                    repository.update(entity)
                }
            }

            is CRUDAction.Delete -> viewModelScope.launch {
                repository.delete(pager.idPredicate(action.id))
            }

            CRUDAction.Refresh -> pager.refresh()
        }
    }
}
