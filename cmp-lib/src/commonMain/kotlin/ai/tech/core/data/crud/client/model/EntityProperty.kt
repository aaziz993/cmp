package ai.tech.core.data.crud.client.model

import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.misc.type.model.Property
import ai.tech.core.misc.type.multiple.iterable.takeIfNotEmpty
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState

public interface EntityProperty : Property {

    public val isId: Boolean
    public fun predicate(state: SearchFieldState): BooleanVariable?
}

public fun List<EntityProperty>.predicate(states: List<SearchFieldState>): BooleanVariable? =
    zip(states).map { (property, state) ->
        if (state.query.isEmpty()) {
            return null
        }

        property.predicate(state)
    }.filterNotNull().takeIfNotEmpty()?.reduce { acc, v -> acc.and(v) }
