package ai.tech.core.data.database

import ai.tech.core.data.database.model.LimitOffset
import ai.tech.core.data.database.model.Order
import ai.tech.core.data.database.model.Page
import ai.tech.core.data.expression.AggregateExpression
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import kotlinx.coroutines.flow.Flow

public interface CRUDRepository<T : Any> {

    public suspend fun <R> transactional(byUser: String? = null, block: suspend CRUDRepository<T>.() -> R): R

    public suspend fun insert(entities: List<T>)

    public suspend fun insert(vararg entities: T): Unit = insert(entities.toList())

    public suspend fun updateSafe(entities: List<T>): List<Boolean>

    public suspend fun updateSafe(vararg entities: T): List<Boolean> = updateSafe(entities.toList())

    public suspend fun update(
        entities: List<Map<String, Any?>>,
        predicate: BooleanVariable? = null,
    ): List<Long>

    public suspend fun find(
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
    ): Flow<T>

    public suspend fun find(
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
        limitOffset: LimitOffset
    ): Page<T>

    public suspend fun find(
        projections: List<Variable>,
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
    ): Flow<List<Any?>>

    public suspend fun find(
        projections: List<Variable>,
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
        limitOffset: LimitOffset
    ): Page<List<Any?>>

    public suspend fun delete(predicate: BooleanVariable? = null): Long

    public suspend fun <T> aggregate(
        aggregate: AggregateExpression<T>,
        predicate: BooleanVariable? = null,
    ): T
}
