package ai.tech.core.presentation.component.lazycolumn.crud.viewmodel

import ai.tech.core.data.crud.model.Order
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState

public sealed interface CRUDAction<T : Any> {
    public data class Find(
        val sort: List<Order>? = null,
        val searchFieldStates: List<SearchFieldState>,
    ) : CRUDAction<Nothing>

    public data object Add : CRUDAction<Nothing>

    public data object CopySelected : CRUDAction<Nothing>

    public data object RemoveSelected : CRUDAction<Nothing>

    public data object EditSelected : CRUDAction<Nothing>

    public data object SaveSelected : CRUDAction<Nothing>

    public data object DeleteSelected : CRUDAction<Nothing>

    public data object Refresh : CRUDAction<Nothing>
}
