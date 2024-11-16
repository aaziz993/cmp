package ai.tech.core.presentation.component.lazycolumn.crud.viewmodel

import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Order
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState

public sealed class CRUDAction<T : Any> {
    public data class Find(
        val sort: List<Order>? = null,
        val searchFieldStates: List<SearchFieldState>,
        val limitOffset: LimitOffset
    ) : CRUDAction<Nothing>()

    public data class Delete(val ids: List<Any>) : CRUDAction<Nothing>()

    public data object Append : CRUDAction<Nothing>()

    public data class Edit(val ids: List<Any>) : CRUDAction<Nothing>()

    public data class Copy(val ids: List<Any>) : CRUDAction<Nothing>()

    public data class Select(val ids: List<Any>) : CRUDAction<Nothing>()

    public data object SelectAll : CRUDAction<Nothing>()
}
