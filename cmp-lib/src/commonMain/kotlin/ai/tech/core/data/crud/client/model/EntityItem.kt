package ai.tech.core.data.crud.client.model

public data class EntityItem<T : Any>(
    val entity: T,
    val id: Any,
    val values: List<String>,
    val modification: Modification? = null,
    val isSelected: Boolean = false,
) {

    val isEdit: Boolean = modification == Modification.EDIT

    val isNew: Boolean = modification == Modification.NEW

    val isModify: Boolean = modification != null

    val isSelectedExist: Boolean = isSelected && !isNew

    val isSelectedEdit: Boolean = isSelected && isEdit

    val isSelectedNew: Boolean = isSelected && isNew

    val isSelectedModify: Boolean = isSelected && isModify

    val isMutated: Boolean = isSelected || modification != null

    public fun validate(properties: List<EntityProperty>): Boolean =
        properties.withIndex().all { (index, property) ->
            property.validator?.validate(values[index]?.toString().orEmpty())?.isEmpty() != false
        }
}

internal val <T : Any> List<EntityItem<T>>.unselected
    get() = filterNot(EntityItem<T>::isSelected)

internal val <T : Any> List<EntityItem<T>>.selected
    get() = filter(EntityItem<T>::isSelected)

internal val <T : Any> List<EntityItem<T>>.isSelectedAny
    get() = any(EntityItem<T>::isSelected)

internal val <T : Any> List<EntityItem<T>>.isSelectedAll
    get() = all(EntityItem<T>::isSelected)

internal val <T : Any> List<EntityItem<T>>.exists
    get() = filterNot(EntityItem<T>::isNew)

internal val <T : Any> List<EntityItem<T>>.selectedExists
    get() = filter(EntityItem<T>::isSelectedExist)

internal val <T : Any> List<EntityItem<T>>.isSelectedAnyExists
    get() = any(EntityItem<T>::isSelectedExist)

internal val <T : Any> List<EntityItem<T>>.edits
    get() = filter(EntityItem<T>::isEdit)

internal val <T : Any> List<EntityItem<T>>.selectedEdits
    get() = filter(EntityItem<T>::isSelectedEdit)

internal val <T : Any> List<EntityItem<T>>.isEditsSelectedAll
    get() = edits.isSelectedAll

internal val <T : Any> List<EntityItem<T>>.news
    get() = filter(EntityItem<T>::isNew)

internal val <T : Any> List<EntityItem<T>>.isSelectedAnyNews
    get() = any(EntityItem<T>::isSelectedNew)

internal val <T : Any> List<EntityItem<T>>.modifies
    get() = filter(EntityItem<T>::isModify)

internal val <T : Any> List<EntityItem<T>>.selectedModifies
    get() = filter(EntityItem<T>::isSelectedModify)

internal fun <T : Any> List<EntityItem<T>>.validate(properties: List<EntityProperty>): Boolean = all { it.validate(properties) }

internal val <T : Any> List<EntityItem<T>>.mutations
    get() = filter(EntityItem<T>::isMutated)
