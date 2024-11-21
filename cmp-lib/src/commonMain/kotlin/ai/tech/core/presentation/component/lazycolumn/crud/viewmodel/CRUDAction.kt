package ai.tech.core.presentation.component.lazycolumn.crud.viewmodel

import ai.tech.core.data.crud.client.model.MutationItem
import ai.tech.core.data.crud.model.Order
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState

public sealed interface CRUDAction<T : Any> {
    public data class Load(
        val sort: List<Order>? = null,
        val searchFieldStates: List<SearchFieldState>,
    ) : CRUDAction<Nothing>

    public data object New : CRUDAction<Nothing>

    public data class NewFrom<T : Any>(val item: MutationItem<T>) : CRUDAction<T>

    public data object NewFromSelected : CRUDAction<Nothing>

    public data object RemoveSelected : CRUDAction<Nothing>

    public data object EditSelected : CRUDAction<Nothing>

    public data object SaveSelected : CRUDAction<Nothing>

    public data object DeleteSelected : CRUDAction<Nothing>

    public data class SetValue(val id: Any, val index: Int, val value: String) : CRUDAction<Nothing>

    public data class Select<T : Any>(val items: List<MutationItem<T>>) : CRUDAction<T>

    public data object UnselectAll : CRUDAction<Nothing>

    public data class RemoveMutations(val ids: List<Any>) : CRUDAction<Nothing>

    public data object Refresh : CRUDAction<Nothing>
}
