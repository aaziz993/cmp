package ai.tech.core.data.database.kotysa

import ai.tech.core.data.crud.AbstractCRUDRepository
import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.database.exp
import ai.tech.core.data.database.kotysa.model.KotysaTable
import ai.tech.core.data.expression.AggregateExpression
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
import ai.tech.core.misc.type.serializer.new
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.TimeZone
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.slf4j.LoggerFactory
import org.ufoss.kotysa.CoroutinesSqlClientDeleteOrUpdate
import org.ufoss.kotysa.CoroutinesSqlClientSelect
import org.ufoss.kotysa.MinMaxColumn
import org.ufoss.kotysa.NumericColumn
import org.ufoss.kotysa.R2dbcSqlClient
import org.ufoss.kotysa.SqlClientQuery
import org.ufoss.kotysa.Table
import org.ufoss.kotysa.WholeNumberColumn

@OptIn(InternalSerializationApi::class)
public abstract class AbstractKotysaCRUDRepository<T : Any, ID : Any>(
    public val client: R2dbcSqlClient,
    public val table: Table<T>,
    private val createEntity: (Map<String, Any?>) -> T,
    createdAtProperty: String? = "createdAt",
    updatedAtProperty: String? = "updatedAt",
    timeZone: TimeZone = TimeZone.UTC,
) : AbstractCRUDRepository<T, ID>(
    createdAtProperty,
    updatedAtProperty,
    timeZone,
) {

    public constructor(kClass: KClass<T>,
                       client: R2dbcSqlClient,
                       table: Table<T>,
                       createdAtProperty: String? = "createdAt",
                       updatedAtProperty: String? = "updatedAt",
                       timeZone: TimeZone = TimeZone.UTC) : this(
        client,
        table,
        { Json.Default.new(kClass.serializer(), it) },
        createdAtProperty,
        updatedAtProperty,
        timeZone,
    )

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val kotysaTable = KotysaTable(table, createdAtProperty, updatedAtProperty)

    override val createdAtNow: ((TimeZone) -> Any)? = kotysaTable.createdAtColumn?.now

    override val updatedAtNow: ((TimeZone) -> Any)? = kotysaTable.updatedAtColumn?.now

    @Suppress("UNCHECKED_CAST")
    private val List<T>.insertable: Array<T>
        get() = if (kotysaTable.createdAtColumn == null) {
            this
        }
        else {
            map { entity -> createEntity(entityCreatedAtAware(entity)) }
        }.toTypedArray<Any>() as Array<T>

    private val T.updatable: T
        get() = createEntity(entityUpdatedAtAware(this))

    override val T.propertyValues: Map<String, Any?>
        get() = kotysaTable.columns.associate { column -> column.name to column[this] }

    final override suspend fun <R> transactional(block: suspend CRUDRepository<T, ID>.() -> R): R =
        client.transactional { block() }!!

    @Suppress("UNCHECKED_CAST")
    final override suspend fun insert(entities: List<T>): Unit = client.insert(*entities.insertable)

    @Suppress("UNCHECKED_CAST")
    final override suspend fun insertAndReturn(entities: List<T>): List<ID> =
        client.insertAndReturn(*entities.insertable).map { kotysaTable.identityColumn[it] as ID }.toList()

    override suspend fun update(entities: List<T>): List<Boolean> = client.transactional {
        if (kotysaTable.updatedAtColumn == null) {
            entities.map { entity -> update(entity).execute() > 0L }
        }
        else {
            entities.map { entity -> update(entity.updatable).execute() > 0L }
        }
    }!!

    final override suspend fun update(
        entities: List<Map<String, Any?>>,
        predicate: BooleanVariable?,
    ): List<Long> = client.transactional {
        if (predicate == null) {
            entities.map { update(it).execute() }
        }
        else {
            entities.map { update(it).predicate(predicate).execute() }
        }
    }!!

    final override suspend fun upsert(entities: List<T>): Nothing = throw UnsupportedOperationException()

    final override fun find(
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset?
    ): Flow<T> = findHelper(sort, predicate, limitOffset)

    override fun find(
        projections: List<Variable>,
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset?
    ): Flow<List<Any?>> = findHelper(projections, sort, predicate, limitOffset)

    override suspend fun delete(predicate: BooleanVariable?): Long = predicate?.let {
        client.deleteFrom(kotysaTable.table).predicate(it).execute()
    } ?: client.deleteAllFrom(kotysaTable.table)

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> aggregate(aggregate: AggregateExpression<T>, predicate: BooleanVariable?): T {
        val column = aggregate.projection?.let { kotysaTable[it.value]!! }?.column

        if (column == null) {
            require(aggregate is Count) {
                "Only in count aggregation column is optional"
            }

            return client.selectCountFrom(kotysaTable.table) as T
        }

        val distinct = aggregate.projection!!.distinct

        return when (aggregate) {
            is Count -> if (distinct) {
                client.selectDistinct(column).andCount(column)
            }
            else {
                client.selectCount(column)
            }

            is Max -> {
                column as MinMaxColumn<*, *>
                if (distinct) {
                    client.selectDistinct(column).andMax(column)
                }
                else {
                    client.selectMax(column)
                }
            }

            is Min -> {
                column as MinMaxColumn<*, *>
                if (distinct) {
                    client.selectDistinct(column).andMin(column)
                }
                else {
                    client.selectMin(column)
                }
            }

            is Avg -> {
                column as NumericColumn<*, *>
                if (distinct) {
                    client.selectDistinct(column).andAvg(column)
                }
                else {
                    client.selectAvg(column)
                }
            }

            is Sum -> {
                column as WholeNumberColumn<*, *>
                if (distinct) {
                    client.selectDistinct(column).andSum(column)
                }
                else {
                    client.selectSum(column)
                }
            }
        }.from(kotysaTable.table).apply {
            predicate?.let { predicate(it) }
        }.fetchOne() as T
    }

    private fun update(entity: T): CoroutinesSqlClientDeleteOrUpdate.Return = client.update(kotysaTable.table).apply {
        kotysaTable.columns.forEach { column -> column.updateFromEntity(this, entity) }
    }.predicate(kotysaTable.identityColumn.getIdPredicate(entity))

    private fun update(map: Map<String, Any?>): CoroutinesSqlClientDeleteOrUpdate.Update<T> =
        client.update(kotysaTable.table).apply {
            map.entries.forEach { (columnName, value) -> kotysaTable[columnName]!!.updateFromValue(this, value) }
        }

    private fun findHelper(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset? = null): Flow<T> =
        client.selectFrom(kotysaTable.table).wheres().execute(sort, predicate, limitOffset)

    private fun findHelper(
        projections: List<Variable>,
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset? = null,
    ): Flow<List<Any?>> = client.selects().apply {
        projections.filterIsInstance<Projection>().forEach { projection ->
            val column = kotysaTable[projection.value]!!.column

            if (projection.distinct) {
                selectDistinct(column)
            }
            else {
                select(column)
            }.apply {
                projection.alias?.let { `as`(it) }
            }
        }
    }.froms().from(kotysaTable.table).wheres().execute(sort, predicate, limitOffset)

    private fun <R : Any> CoroutinesSqlClientSelect.Wheres<R>.execute(
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset?,
    ): Flow<R> = apply {
        predicate?.let { predicate(it) }

        sort?.forEach { order ->
            val column = kotysaTable[order.name]!!.column

            if (order.ascending) {
                orderByAsc(column)
            }
            else {
                orderByDesc(column)
            }
        }

        limitOffset?.offset?.let { offset(it) }

        limitOffset?.limit?.let { limit(it) }
    }.fetchAll()

    @Suppress("UNCHECKED_CAST")
    private fun <T : SqlClientQuery.Where<T>> SqlClientQuery.Whereable<T>.predicate(predicate: BooleanVariable): T {
        var value: Any? = null

        var expression: Expression? = null

        val logValue = StringBuilder()

        (predicate as Expression).evaluate(
            {
                value = value!!.logicExp(this, (expression!!.arguments[0] as Field), logValue)
                    .compareExp(expression!!, logValue)

                expression = null
            },
        ) {
            when {
                value == null -> {
                    val field = arguments[0] as Field

                    logValue.append("where(${field.value})")

                    value = this@predicate.kotysaExp("where", field).compareExp(this, logValue)
                }

                expression == null -> expression = this

                else -> throw IllegalArgumentException("Unsupported expression tree")
            }
        }

        logger.debug(logValue.toString())

        return value as T
    }

    private fun Any.compareExp(expression: Expression, logValue: StringBuilder): Any {
        val isTemporal = kotysaTable[(expression.arguments[0] as Field).value]!!.isTemporal

        return if (expression is Between) {
            if (isTemporal) {
                kotysaExp("afterOrEq", expression.arguments[1] as Value<*>)
                    .kotysaExp("and", expression.arguments[0] as Value<*>)
                    .kotysaExp("beforeOrEq", expression.arguments[2] as Value<*>)
            }
            else {
                kotysaExp("supOrEq", expression.arguments[1] as Value<*>)
                    .kotysaExp("and", expression.arguments[0] as Value<*>)
                    .kotysaExp("infOrEq", expression.arguments[2] as Value<*>)
            }
        }
        else {
            val kotysaExpName = when (expression) {
                is Equals -> {
                    if (expression.arguments.size == 2) {
                        "eq"
                    }
                    else {
                        val ignoreCase = (expression.arguments[2] as BooleanValue).value

                        val matchAll = (expression.arguments[3] as BooleanValue).value

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
            }

            val otherArg = expression.arguments[1] as Value<*>

            logValue.append(".$kotysaExpName(${otherArg.value})")

            kotysaExp(kotysaExpName, otherArg)
        }
    }

    private fun Any.logicExp(expression: Expression, field: Field, logValue: StringBuilder): Any = when (expression) {
        is And -> "and"
        is Or -> "or"
        else -> throw IllegalArgumentException("Unsupported expression type \"${this::class.simpleName}\"")
    }.let {
        logValue.append(".$it(${field.value})")
        kotysaExp(it, field)
    }

    private fun Any.kotysaExp(name: String, value: Value<*>): Any = exp(name, value) { kotysaTable[it]!! }
}
