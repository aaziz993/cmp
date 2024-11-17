package ai.tech.core.presentation.component.lazycolumn.crud.viewmodel

import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Order
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState

public sealed class CRUDAction {
    public data class Find(
        val sort: List<Order>? = null,
        val searchFieldStates: List<SearchFieldState>,
        val limitOffset: LimitOffset
    ) : CRUDAction()

    public data object New : CRUDAction()

    public data class Copy(val id: Any) : CRUDAction()

    public data class Edit(val id: Any) : CRUDAction()

    public data object EditSelected : CRUDAction()

    public data object CopySelected : CRUDAction()

    public data object Save : CRUDAction()

    public data class Delete(val id: Any) : CRUDAction()

    public data object DeleteSelected : CRUDAction()

    public data class Select(val id: Any) : CRUDAction()

    public data object SelectAll : CRUDAction()

//    public data object Refresh : CRUDAction()
}
