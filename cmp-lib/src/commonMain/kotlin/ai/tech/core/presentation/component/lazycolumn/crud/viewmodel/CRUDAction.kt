package ai.tech.core.presentation.component.lazycolumn.crud.viewmodel

import ai.tech.core.data.crud.model.Order
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState

public sealed interface CRUDAction<T : Any> {
    public data class Find(
        val sort: List<Order>? = null,
        val searchFieldStates: List<SearchFieldState>,
    ) : CRUDAction<Nothing>

    public data object Refresh : CRUDAction<Nothing>
}
