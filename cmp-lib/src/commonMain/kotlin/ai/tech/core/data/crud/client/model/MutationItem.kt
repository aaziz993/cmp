package ai.tech.core.data.crud.client.model

public data class MutationItem<T : Any>(
    val entity: T,
    val id: Any,
    val values: List<Any?>,
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

internal val <T : Any> List<MutationItem<T>>.unselected
    get() = filterNot(MutationItem<T>::isSelected)

internal val <T : Any> List<MutationItem<T>>.selected
    get() = filter(MutationItem<T>::isSelected)

internal val <T : Any> List<MutationItem<T>>.isSelectedAny
    get() = any(MutationItem<T>::isSelected)

internal val <T : Any> List<MutationItem<T>>.isSelectedAll
    get() = all(MutationItem<T>::isSelected)

internal val <T : Any> List<MutationItem<T>>.exists
    get() = filterNot(MutationItem<T>::isNew)

internal val <T : Any> List<MutationItem<T>>.selectedExists
    get() = filter(MutationItem<T>::isSelectedExist)

internal val <T : Any> List<MutationItem<T>>.isSelectedAnyExists
    get() = any(MutationItem<T>::isSelectedExist)

internal val <T : Any> List<MutationItem<T>>.edits
    get() = filter(MutationItem<T>::isEdit)

internal val <T : Any> List<MutationItem<T>>.selectedEdits
    get() = filter(MutationItem<T>::isSelectedEdit)

internal val <T : Any> List<MutationItem<T>>.isEditsSelectedAll
    get() = edits.isSelectedAll

internal val <T : Any> List<MutationItem<T>>.news
    get() = filter(MutationItem<T>::isNew)

internal val <T : Any> List<MutationItem<T>>.isSelectedAnyNews
    get() = any(MutationItem<T>::isSelectedNew)

internal val <T : Any> List<MutationItem<T>>.modifies
    get() = filter(MutationItem<T>::isModify)

internal val <T : Any> List<MutationItem<T>>.selectedModifies
    get() = filter(MutationItem<T>::isSelectedModify)

internal fun <T : Any> List<MutationItem<T>>.validate(properties: List<EntityProperty>): Boolean = all { it.validate(properties) }

internal val <T : Any> List<MutationItem<T>>.mutations
    get() = filter(MutationItem<T>::isMutated)
