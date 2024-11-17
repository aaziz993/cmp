package ai.tech.core.presentation.component.lazycolumn.crud.model

import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.f
import ai.tech.core.data.validator.Validator
import ai.tech.core.misc.type.kClass
import ai.tech.core.misc.type.parsePrimeOrNull
import ai.tech.core.misc.type.primeTypeOrNull
import ai.tech.core.presentation.component.textfield.search.model.SearchFieldState
import kotlin.reflect.typeOf
import kotlinx.serialization.descriptors.SerialDescriptor

public data class KotysaColumn(
    override val name: String,
    override val header: String = name,
    override val descriptor: SerialDescriptor,
    override val isId: Boolean = false,
    override val isReadOnly: Boolean,
    override val validator: Validator?
) : EntityColumn {

    override fun predicate(state: SearchFieldState): BooleanVariable? = when {
        descriptor.primeTypeOrNull == typeOf<String>() -> {
            if (state.compareMatch == -3) {
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
            if (state.compareMatch == 3) {
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
                        2 -> name.f.gt(it)
                        1 -> name.f.gte(it)
                        0 -> name.f.eq(it)
                        -2 -> name.f.lt(it)
                        -1 -> name.f.lte(it)
                        -3 -> name.f.neq(it)
                        else -> throw IllegalStateException()
                    }
                }
            }
        }
    }
}
