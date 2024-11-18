package ai.tech.core.presentation.component.lazycolumn.crud.model

public data class Item<T : Any>(
    val entity: T,
    val id: Any,
    val values: List<Any?>,
    val isNew: Boolean = false,
    val isEditing: Boolean = false,
    val isSelected: Boolean = false,
) {

    val isReadOnly: Boolean = !(isEditing || isNew)

    val isNecessary: Boolean = isSelected || isEditing || isNew

    public fun validate(properties: List<EntityColumn>): Boolean =
        properties.withIndex().all { (index, property) ->
            property.validator?.validate(values[index]?.toString().orEmpty())?.isEmpty() != false
        }
}

public val <T : Any> List<Item<T>>.isSelectedAny: Boolean
    get() = any(Item<T>::isSelected)

public val <T : Any> List<Item<T>>.isSelectedAll: Boolean
    get() = all(Item<T>::isSelected)

public val <T : Any> List<Item<T>>.isSelectedAnyNew: Boolean
    get() = any { it.isSelected && it.isNew }

public val <T : Any> List<Item<T>>.isSelectedAllExistsIsEditing: Boolean
    get() = all { it.isSelected && it.isEditing && !it.isNew }

public val <T : Any> List<Item<T>>.isSelectedAnyExists: Boolean
    get() = any { it.isSelected && !it.isNew }

public val <T : Any> List<Item<T>>.selected: List<Item<T>>
    get() = filter(Item<T>::isSelected)

public val <T : Any> List<Item<T>>.selectedExists: List<Item<T>>
    get() = filter { it.isSelected && !it.isNew }
