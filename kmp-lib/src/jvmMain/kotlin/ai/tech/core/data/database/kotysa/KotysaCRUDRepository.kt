package ai.tech.core.data.database.kotysa

import ai.tech.core.data.database.crud.CRUDRepository
import ai.tech.core.data.database.kotysa.model.KotysaTable
import ai.tech.core.data.database.crud.model.LimitOffset
import ai.tech.core.data.database.crud.model.Order
import ai.tech.core.data.database.crud.model.Page
import ai.tech.core.data.expression.AggregateExpression
import ai.tech.core.data.expression.AggregateExpression.Companion.count
import ai.tech.core.data.expression.And
import ai.tech.core.data.expression.Avg
import ai.tech.core.data.expression.Between
import ai.tech.core.data.expression.BooleanValue
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Count
import ai.tech.core.data.expression.Equals
import ai.tech.core.data.expression.Expression
import ai.tech.core.data.expression.Field
import ai.tech.core.data.expression.GreaterEqualThan
import ai.tech.core.data.expression.GreaterThan
import ai.tech.core.data.expression.In
import ai.tech.core.data.expression.LessEqualThan
import ai.tech.core.data.expression.LessThan
import ai.tech.core.data.expression.Max
import ai.tech.core.data.expression.Min
import ai.tech.core.data.expression.NotEquals
import ai.tech.core.data.expression.NotIn
import ai.tech.core.data.expression.Or
import ai.tech.core.data.expression.Projection
import ai.tech.core.data.expression.Sum
import ai.tech.core.data.expression.Value
import ai.tech.core.data.expression.Variable
import ai.tech.core.misc.type.copy
import ai.tech.core.misc.type.kClass
import kotlin.reflect.KClass
import kotlin.reflect.KTypeParameter
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubclassOf
import kotlin.reflect.full.memberFunctions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.TimeZone
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import org.ufoss.kotysa.CoroutinesSqlClientDeleteOrUpdate
import org.ufoss.kotysa.CoroutinesSqlClientSelect
import org.ufoss.kotysa.MinMaxColumn
import org.ufoss.kotysa.NumericColumn
import org.ufoss.kotysa.R2dbcSqlClient
import org.ufoss.kotysa.SqlClientQuery
import org.ufoss.kotysa.Table
import org.ufoss.kotysa.WholeNumberColumn

public abstract class KotysaCRUDRepository<T : Any>(
    private val serializer: KSerializer<T>,
    private val client: R2dbcSqlClient,
    table: Table<T>,
    private val createdAtProperty: String? = "createdAt",
    private val updatedAtProperty: String? = "updatedAt",
    timeZone: String? = null
) : CRUDRepository<T> {

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val table = KotysaTable(table, createdAtProperty, updatedAtProperty)

    private val timeZone: TimeZone = timeZone?.let { TimeZone.of(it) } ?: TimeZone.UTC

    final override suspend fun <R> transactional(byUser: String?, block: suspend CRUDRepository<T>.() -> R): R =
        client.transactional {
            block()
        }!!

    @Suppress("UNCHECKED_CAST")
    final override suspend fun insert(entities: List<T>) {
        client.insert(
            *((table.createdAtColumn?.let {
                val temporal = it.value.now!!(timeZone)
                entities.map { Json.Default.copy(serializer, it) { mapOf(createdAtProperty!! to temporal) } }
            } ?: entities).toTypedArray<Any>() as Array<T>),
        )
    }

    override suspend fun updateSafe(entities: List<T>): List<Boolean> = client.transactional {
        table.updatedAtColumn?.let {
            val temporal = it.value.now!!(timeZone)
            entities.map { update(Json.Default.copy(serializer, it) { mapOf(updatedAtProperty!! to temporal) }).execute() > 0L }
        } ?: entities.map { update(it).execute() > 0L }
    }!!

    final override suspend fun update(
        entities: List<Map<String, Any?>>,
        predicate: BooleanVariable?,
    ): List<Long> = client.transactional {
        predicate?.let { p ->
            entities.map { update(it).predicate(p).execute() }
        } ?: entities.map { update(it).execute() }
    }!!

    final override suspend fun find(
        sort: List<Order>?,
        predicate: BooleanVariable?,
    ): Flow<T> = findHelper(sort, predicate)

    override suspend fun find(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset): Page<T> =
        client.transactional {
            Page(findHelper(sort, predicate, limitOffset).toList(), aggregate(count(), predicate))
        }!!

    override suspend fun find(
        projections: List<Variable>, sort: List<Order>?, predicate: BooleanVariable?
    ): Flow<List<Any?>> = findHelper(projections, sort, predicate)

    override suspend fun find(
        projections: List<Variable>, sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset
    ): Page<List<Any?>> = client.transactional {
        Page(
            findHelper(projections, sort, predicate, limitOffset).toList(), aggregate(count(), predicate),
        )
    }!!

    override suspend fun delete(predicate: BooleanVariable?): Long = predicate?.let {
        client.deleteFrom(table.table).predicate(it).execute()
    } ?: client.deleteAllFrom(table.table)

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> aggregate(aggregate: AggregateExpression<T>, predicate: BooleanVariable?): T =
        when (aggregate) {
            is Count -> aggregate.projection?.let { client.selectCount(table[it.value].column).from(table.table) }
                ?: client.selectCountFrom(table.table)

            is Max -> client.selectMax(table[aggregate.projection.value].column as MinMaxColumn<*, *>).from(table.table)
            is Min -> client.selectMin(table[aggregate.projection.value].column as MinMaxColumn<*, *>).from(table.table)
            is Avg -> client.selectAvg(table[aggregate.projection.value].column as NumericColumn<*, *>)
                .from(table.table)

            is Sum -> client.selectSum(table[aggregate.projection.value].column as WholeNumberColumn<*, *>)
                .from(table.table)
        }.let { select ->
            predicate?.let {
                select.predicate(it)
            } ?: select
        }.fetchOne() as T

    private fun update(entity: T): CoroutinesSqlClientDeleteOrUpdate.Return = client.update(table.table).let {
        table.columns.values.fold(it) { acc, v -> v.updateFromEntity(acc, entity) }
    }.predicate(table.getIdPredicate(entity))

    private fun update(map: Map<String, Any?>): CoroutinesSqlClientDeleteOrUpdate.Update<T> =
        client.update(table.table).let {
            map.entries.fold(it) { acc, (k, v) -> table[k].updateFromValue(acc, v) }
        }

    private fun findHelper(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset? = null): Flow<T> =
        client.selectFrom(table.table).wheres().execute(sort, predicate, limitOffset)

    private fun findHelper(
        projections: List<Variable>,
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset? = null,
    ): Flow<List<Any?>> = client.selects().let {
        projections.filterIsInstance<Projection>().fold(it) { acc, v ->
            table[v.value].let {
                if (v.distinct) {
                    acc.selectDistinct(it.column)
                }
                else {
                    acc.select(it.column)
                }.let { select ->
                    v.alias?.let { select.`as`(it) } ?: select
                }
            }
        }
    }.froms().from(table.table).wheres().execute(sort, predicate, limitOffset)

    private fun <R : Any> CoroutinesSqlClientSelect.Wheres<R>.execute(
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset?,
    ): Flow<R> = (predicate?.let { predicate(it) } ?: this).let {
        sort?.fold(it.ordersBy()) { acc, v ->
            if (v.ascending) {
                acc.orderByAsc(table[v.name].column)
            }
            else {
                acc.orderByDesc(table[v.name].column)
            }
        } ?: it
    }.let { select ->
        limitOffset?.offset?.let { select.offset(it) } ?: select
    }.let { select ->
        limitOffset?.limit?.let { select.limit(it) } ?: select
    }.fetchAll()

    @Suppress("UNCHECKED_CAST")
    private fun <T : SqlClientQuery.Where<T>> SqlClientQuery.Whereable<T>.predicate(predicate: BooleanVariable): T {
        var value: Any? = null
        var expression: Expression? = null

        val logValue = StringBuilder()

        (predicate as Expression).evaluate(
            { _ ->
                when {
                    value == null -> {
                        val field = arguments[0] as Field

                        logValue.append("where(${field.value})")
                        value = this@predicate.exp("where", field).compareExp(this, logValue)
                    }

                    expression == null -> expression = this

                    else -> throw IllegalArgumentException("Unsupported expression tree")
                }
            },
        ) {
            value = value!!.logicExp(this, (expression!!.arguments[0] as Field), logValue)
                .compareExp(expression!!, logValue)

            expression = null
        }

        logger.debug(logValue.toString())

        return value as T
    }

    private fun Any.compareExp(expression: Expression, logValue: StringBuilder): Any {
        val isTemporal = table[(expression.arguments[0] as Field).value].isTemporal

        return if (expression is Between) {
            if (isTemporal) {
                exp("afterOrEq", expression.arguments[1] as Value<*>).exp("and", expression.arguments[0] as Value<*>)
                    .exp("beforeOrEq", expression.arguments[2] as Value<*>)
            }
            else {
                exp("supOrEq", expression.arguments[1] as Value<*>).exp("and", expression.arguments[0] as Value<*>)
                    .exp("infOrEq", expression.arguments[2] as Value<*>)
            }
        }
        else {
            when (expression) {
                is Equals -> {
                    if (expression.arguments.size == 2) {
                        "eq"
                    }
                    else {
                        val matchAll = (expression.arguments[2] as BooleanValue).value
                        val ignoreCase = (expression.arguments[3] as BooleanValue).value
                        if (matchAll) {
                            "eq"
                        }
                        else if (ignoreCase) {
                            "containsIgnoreCase"
                        }
                        else {
                            "contains"
                        }
                    }
                }

                is NotEquals -> "notEq"

                is GreaterThan -> if (isTemporal) {
                    "after"
                }
                else {
                    "sup"
                }

                is GreaterEqualThan -> if (isTemporal) {
                    "afterOrEq"
                }
                else {
                    "supOrEq"
                }

                is LessThan -> if (isTemporal) {
                    "before"
                }
                else {
                    "inf"
                }

                is LessEqualThan -> if (isTemporal) {
                    "beforeOrEq"
                }
                else {
                    "infOrEq"
                }

                is In -> "in"

                is NotIn -> "notIn"

                else -> throw IllegalArgumentException("Unsupported expression type \"${this::class.simpleName}\"")
            }.let {
                val otherArg = expression.arguments[1] as Value<*>

                logValue.append(".$it(${otherArg.value})")

                exp(it, otherArg)
            }
        }
    }

    private fun Any.logicExp(expression: Expression, field: Field, logValue: StringBuilder): Any = when (expression) {
        is And -> "and"
        is Or -> "or"
        else -> throw IllegalArgumentException("Unsupported expression type \"${this::class.simpleName}\"")
    }.let {
        logValue.append(".$it(${field.value})")
        exp(it, field)
    }

    private fun Any.exp(name: String, value: Value<*>): Any {
        val vkClass: KClass<*>
        val v: Any?

        when (value) {
            is Field -> table[value.value].column.let {
                vkClass = it::class
                v = it
            }

            else -> {
                vkClass = value::class.declaredMemberProperties.find { it.name == "value" }!!.returnType.kClass
                v = value.value
            }
        }

        return this::class.memberFunctions.filter { it.name == name && it.parameters.size == 2 }.let {
            it.find { (it.parameters[1].type.classifier !is KTypeParameter) && vkClass.isSubclassOf(it.parameters[1].type.kClass) }
                ?: it.find { it.parameters[1].type.classifier is KTypeParameter }
        }!!.call(this, v)!!
    }
}

