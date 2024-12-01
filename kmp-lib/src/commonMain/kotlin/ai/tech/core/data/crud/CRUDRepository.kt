package ai.tech.core.data.crud

import ai.tech.core.data.crud.model.query.LimitOffset
import ai.tech.core.data.crud.model.query.Order
import ai.tech.core.data.expression.AggregateExpression
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import ai.tech.core.data.transaction.Transaction
import kotlinx.coroutines.flow.Flow

public interface CRUDRepository<T : Any> {

    public suspend fun <R> transactional(block: suspend CRUDRepository<T>.(Transaction) -> R): R

    public suspend fun insertAndReturn(entities: List<T>): List<T>

    public suspend fun insertAndReturn(vararg entities: T): List<T> = insertAndReturn(entities.toList())

    public suspend fun insert(entities: List<T>)

    public suspend fun insert(vararg entities: T): Unit = insert(entities.toList())

    public suspend fun update(entities: List<T>): List<Boolean>

    public suspend fun update(vararg entities: T): List<Boolean> = update(entities.toList())

    public suspend fun update(
        propertyValues: List<Map<String, Any?>>,
        predicate: BooleanVariable? = null,
    ): Long

    public suspend fun upsert(entities: List<T>): List<T>

    public suspend fun upsert(vararg entities: T): List<T> = upsert(entities.toList())

    public fun find(
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
        limitOffset: LimitOffset? = null
    ): Flow<T>

    public fun find(
        projections: List<Variable>,
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
        limitOffset: LimitOffset? = null
    ): Flow<List<Any?>>

    public suspend fun delete(predicate: BooleanVariable? = null): Long

    public suspend fun <T> aggregate(
        aggregate: AggregateExpression<T>,
        predicate: BooleanVariable? = null,
    ): T
}
