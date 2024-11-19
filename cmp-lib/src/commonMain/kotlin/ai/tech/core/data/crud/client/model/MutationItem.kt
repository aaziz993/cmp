package ai.tech.core.data.crud.client.model

public data class MutationItem<T : Any>(
    val entity: T,
    val id: Any,
    val values: List<Any?>,
    val mutation: Mutation? = null,
    val isSelected: Boolean = false,
) {

    val isNew: Boolean = mutation == Mutation.NEW

    val isEditing: Boolean = mutation == Mutation.EDIT

    val isActual: Boolean = isSelected || mutation != null

    public fun validate(properties: List<EntityColumn>): Boolean =
        properties.withIndex().all { (index, property) ->
            property.validator?.validate(values[index]?.toString().orEmpty())?.isEmpty() != false
        }
}
