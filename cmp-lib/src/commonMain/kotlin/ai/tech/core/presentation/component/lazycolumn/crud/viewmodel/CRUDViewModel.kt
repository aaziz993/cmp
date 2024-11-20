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
    create: (id: Any) -> T,
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
            create,
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
            is CRUDAction.Find -> pager.refresh(action.sort, pager.properties.predicate(action.searchFieldStates))

            CRUDAction.Add -> pager.add()

            CRUDAction.CopySelected -> pager.copySelected()

            CRUDAction.RemoveSelected -> pager.removeSelected()

            CRUDAction.EditSelected -> pager.editSelected()

            CRUDAction.SaveSelected -> viewModelScope.launch{
                pager.getSelectedModifies()
            }

            CRUDAction.DeleteSelected -> {
                viewModelScope.launch {
                    repository.delete(pager.getSelectedIdPredicate())
                    pager.deleteSelected()
                    pager.refresh()
                }
            }
        }

        CRUDAction.Refresh -> pager.refresh()
    }
}
}
