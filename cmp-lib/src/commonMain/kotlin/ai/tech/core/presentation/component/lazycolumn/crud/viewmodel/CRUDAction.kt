package ai.tech.core.presentation.component.lazycolumn.crud.viewmodel

import ai.tech.core.data.crud.client.model.EntityItem
import ai.tech.core.data.crud.model.Order
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState

public sealed interface CRUDAction<out T:Any> {
    public data class Find(
        val sort: List<Order>? = null,
        val searchFieldStates: List<SearchFieldState>,
    ) : CRUDAction<Nothing>

    public data object New : CRUDAction<Nothing>

    public data class NewFrom<T : Any>(val item: EntityItem<T>) : CRUDAction<T>

    public data class EditOrUnEdit<T : Any>(val item: EntityItem<T>) : CRUDAction<T>

    public data class SelectOrUnselect<T : Any>(val item: EntityItem<T>) : CRUDAction<T>

    public data object NewFromSelected : CRUDAction<Nothing>

    public data class Remove(val id: Any) : CRUDAction<Nothing>

    public data class SetValue(val id: Any, val index: Int, val value: String) : CRUDAction<Nothing>

    public data class SelectAll<T : Any>(val items: List<EntityItem<T>>) : CRUDAction<T>

    public data object UnselectAll : CRUDAction<Nothing>

    public data object RemoveSelected : CRUDAction<Nothing>

    public data object EditSelected : CRUDAction<Nothing>

    public data object SaveSelected : CRUDAction<Nothing>

    public data object DeleteSelected : CRUDAction<Nothing>

    public data class Save<T : Any>(val item: EntityItem<T>) : CRUDAction<T>

    public data class Delete(val id: Any) : CRUDAction<Nothing>

    public data object Refresh : CRUDAction<Nothing>
}
