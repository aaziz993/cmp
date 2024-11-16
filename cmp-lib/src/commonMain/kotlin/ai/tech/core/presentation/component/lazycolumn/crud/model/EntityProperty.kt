package ai.tech.core.presentation.component.lazycolumn.crud.model

import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.misc.type.model.Property
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState

public interface EntityProperty : Property {

    public fun predicate(state: SearchFieldState): BooleanVariable?
}
