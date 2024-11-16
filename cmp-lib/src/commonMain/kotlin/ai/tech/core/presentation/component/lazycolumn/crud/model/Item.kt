package ai.tech.core.presentation.component.lazycolumn.crud.model

public data class Item<T : Any>(
    val entity: T,
    val id: Any? = null,
    val values: List<Any?>,
    val append: Boolean = false,
    val edit: Boolean = append,
    val select: Boolean = false,
) {

    val modify: Boolean = select || edit || append
}
