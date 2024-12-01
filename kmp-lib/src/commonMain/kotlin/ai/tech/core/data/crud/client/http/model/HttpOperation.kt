package ai.tech.core.data.crud.client.http.model

import ai.tech.core.data.crud.model.query.LimitOffset
import ai.tech.core.data.crud.model.query.Order
import ai.tech.core.data.expression.AggregateExpression
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
public sealed class HttpOperation {

    public data class Insert<out T : Any>(val values: List<T>) : HttpOperation()

    public data class InsertAndReturn<out T : Any>(val values: List<T>) : HttpOperation()

    public data class Update<out T : Any>(val values: List<T>) : HttpOperation()

    public data class UpdateUntyped(val propertyValues: JsonElement, val predicate: BooleanVariable?) : HttpOperation()

    public data class Upsert<out T : Any>(val values: List<T>) : HttpOperation()

    public data class Find(
        val projections: List<Variable>?,
        val sort: List<Order>?,
        val predicate: BooleanVariable?,
        val limitOffset: LimitOffset?
    ) : HttpOperation()

    public data class Delete(val predicate: BooleanVariable?) : HttpOperation()

    public data class Aggregate(val aggregate: AggregateExpression<*>, val predicate: BooleanVariable?) : HttpOperation()
}
