package ai.tech.core.presentation.component.lazycolumn.crud.viewmodel

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.client.AbstractCRUDMutablePager
import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.model.predicate
import ai.tech.core.data.crud.model.Order
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
    create: (Map<String, Any?>?) -> T,
    properties: List<EntityProperty>,
    getValues: (T) -> List<Any?>,
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
            getValues,
            config,
            initialKey,
            remoteMediator,
            firstItemOffset,
        )

    @OptIn(ExperimentalPagingApi::class)
    override fun action(action: CRUDAction<T>) {
        when (action) {
            is CRUDAction.Load -> pager.load(action.sort, pager.properties.predicate(action.searchFieldStates))

            CRUDAction.New -> pager.new()

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

            is CRUDAction.SetValue -> pager.setValue(action.id, action.index, action.value)

            is CRUDAction.Select -> pager.selectAll(action.items)

            CRUDAction.UnselectAll -> pager.unselectAll()

            is CRUDAction.RemoveMutations -> pager.remove(action.ids)

            CRUDAction.Refresh -> pager.refresh()
        }
    }
}
