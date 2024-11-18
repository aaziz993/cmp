package ai.tech.core.presentation.component.lazycolumn.crud.viewmodel

import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Order
import ai.tech.core.presentation.component.lazycolumn.crud.model.Item
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState

public sealed interface CRUDAction<out T : Any> {
    public data class Find(
        val sort: List<Order>? = null,
        val searchFieldStates: List<SearchFieldState>,
        val limitOffset: LimitOffset
    ) : CRUDAction<Nothing>

    public data object Add : CRUDAction<Nothing>

    public data class Copy<T:Any>(val item: Item<T>) : CRUDAction<T>

    public data class Remove(val id: Any) : CRUDAction<Nothing>

    public data object RemoveSelected : CRUDAction<Nothing>

    public data class Edit(val id: Any) : CRUDAction<Nothing>

    public data object EditSelected : CRUDAction<Nothing>

    public data class ChangeValue(val id: Any,val index:Int,val value: String) : CRUDAction<Nothing>

    public data object CopySelected : CRUDAction<Nothing>

    public data class Upload<T : Any>(val entities: List<T>) : CRUDAction<T>

    public data class Save<T : Any>(val entity: T) : CRUDAction<T>

    public data object SaveSelected : CRUDAction<Nothing>

    public data class Delete(val id: Any) : CRUDAction<Nothing>

    public data object DeleteSelected : CRUDAction<Nothing>

    public data class Select(val id: Any) : CRUDAction<Nothing>

    public data object SelectAll : CRUDAction<Nothing>

//    public data object Refresh : CRUDAction()
}
