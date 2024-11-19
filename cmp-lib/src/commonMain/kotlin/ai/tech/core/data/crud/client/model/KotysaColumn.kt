package ai.tech.core.data.crud.client.model

import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.f
import ai.tech.core.data.validator.Validator
import ai.tech.core.misc.type.kClass
import ai.tech.core.misc.type.parsePrimeOrNull
import ai.tech.core.misc.type.primeTypeOrNull
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldCompare
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState
import kotlin.reflect.typeOf
import kotlinx.serialization.descriptors.SerialDescriptor

public data class KotysaColumn(
    override val name: String,
    override val descriptor: SerialDescriptor,
    override val isId: Boolean = false,
    override val isReadOnly: Boolean,
    override val validator: Validator?
) : EntityColumn {

    override fun predicate(state: SearchFieldState): BooleanVariable? = when {
        descriptor.primeTypeOrNull == typeOf<String>() -> {
            if (state.compareMatch == SearchFieldCompare.NOT_EQUAL) {
                name.f.neq(state.query)
            }
            else if (state.regexMatch) {
                name.f.eqPattern(
                    state.query,
                    state.wordMatch,
                    !state.caseMatch,
                )
            }
            else {
                name.f.eq(
                    state.query,
                    state.wordMatch,
                    !state.caseMatch,
                )
            }
        }

        else -> {
            if (state.compareMatch == SearchFieldCompare.BETWEEN) {
                val left = state.query.substringBefore("..").ifEmpty { null }
                    ?.let { descriptor.primeTypeOrNull!!.kClass.parsePrimeOrNull(it) }
                val right = state.query.substringAfter("..").ifEmpty { null }
                    ?.let { descriptor.primeTypeOrNull!!.kClass.parsePrimeOrNull(it) }
                if (!(left == null || right == null)) {
                    name.f.between(left, right)
                }
                else {
                    null
                }
            }
            else {
                descriptor.primeTypeOrNull!!.kClass.parsePrimeOrNull(state.query)?.let {
                    when (state.compareMatch) {
                        SearchFieldCompare.GREATER_THAN -> name.f.gt(it)
                        SearchFieldCompare.GREATER_THAN_EQUAL -> name.f.gte(it)
                        SearchFieldCompare.EQUALS -> name.f.eq(it)
                        SearchFieldCompare.LESS_THAN -> name.f.lt(it)
                        SearchFieldCompare.LESS_THAN_EQUAL -> name.f.lte(it)
                        SearchFieldCompare.NOT_EQUAL -> name.f.neq(it)
                        else -> throw IllegalStateException()
                    }
                }
            }
        }
    }
}
