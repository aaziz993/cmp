package ai.tech.core.data.crud.client.model

import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.misc.type.model.Property
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState

public interface EntityColumn : Property {

    public val isId: Boolean
    public fun predicate(state: SearchFieldState): BooleanVariable?
}
