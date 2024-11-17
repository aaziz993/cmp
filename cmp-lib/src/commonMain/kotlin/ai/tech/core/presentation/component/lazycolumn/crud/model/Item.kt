package ai.tech.core.presentation.component.lazycolumn.crud.model

public data class Item<T : Any>(
    val entity: T,
    val id: Any,
    val values: List<Any?>,
    val isNew: Boolean = false,
    val isEditing: Boolean = false,
    val isSelected: Boolean = false,
) {

    val isModifying: Boolean = isSelected || isEditing || isNew

    public fun validate(properties: List<EntityColumn>): Boolean =
        properties.withIndex().all { (index, property) ->
            property.validator?.validate(values[index]?.toString().orEmpty())?.isEmpty() != false
        }
}

public val <T:Any> List<Item<T>>.selected:List<Item<T>>
    get() = filter(Item<T>::isSelected)

public val <T:Any> List<Item<T>>.selectedNotNew
    get() = selected.filterNot(Item<T>::isNew)

public val <T:Any> List<Item<T>>.selectedEditing
    get() = filter(Item<T>::isSelected)
