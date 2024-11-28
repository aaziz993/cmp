@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package ai.tech.core.data.database.kotysa

import ai.tech.core.data.crud.AbstractTypeCRUDRepository
import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.database.exp
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
import ai.tech.core.data.expression.f
import ai.tech.core.misc.type.multiple.toTypedArray
import ai.tech.core.misc.type.serializer.create
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.toList
import kotlinx.datetime.TimeZone
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.slf4j.LoggerFactory
import org.ufoss.kotysa.AbstractTable
import org.ufoss.kotysa.CoroutinesSqlClientDeleteOrUpdate
import org.ufoss.kotysa.CoroutinesSqlClientSelect
import org.ufoss.kotysa.MinMaxColumn
import org.ufoss.kotysa.NumericColumn
import org.ufoss.kotysa.R2dbcSqlClient
import org.ufoss.kotysa.SqlClientQuery
import org.ufoss.kotysa.WholeNumberColumn
import org.ufoss.kotysa.columns.AbstractDbColumn

@OptIn(InternalSerializationApi::class)
public abstract class AbstractKotysaCRUDRepository<T : Any>(
    public val client: R2dbcSqlClient,
    public val table: AbstractTable<T>,
    createEntity: Map<String, Any?>. () -> T,
    createdAtProperty: String? = "createdAt",
    updatedAtProperty: String? = "updatedAt",
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
) : AbstractTypeCRUDRepository<T>(
    createEntity,
    createdAtProperty,
    updatedAtProperty,
    timeZone,
) {

    public constructor(kClass: KClass<T>,
                       client: R2dbcSqlClient,
                       table: AbstractTable<T>,
                       createdAtProperty: String? = "createdAt",
                       updatedAtProperty: String? = "updatedAt",
                       timeZone: TimeZone = TimeZone.UTC) : this(
        client,
        table,
        { Json.Default.create(kClass.serializer(), this) },
        createdAtProperty,
        updatedAtProperty,
        timeZone,
    )

    init {
        require(table.kotysaPk.columns.size == 1) {
            "Only table with one identity column primary key is permitted"
        }
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val columns = table.kotysaColumns.filterIsInstance<AbstractDbColumn<T, *>>()

    override val T.propertyValues: Map<String, Any?>
        get() = columns.associate { column -> column.name to column.entityGetter(this) }

    override val createdAtNow: ((TimeZone) -> Any)? = createdAtProperty?.let { property -> table[property]!! }?.now

    override val updatedAtNow: ((TimeZone) -> Any)? = updatedAtProperty?.let { property -> table[property]!! }?.now

    private val columnUpdaters = table.kotysaColumns.filterIsInstance<AbstractDbColumn<T, Any>>().associate { it to it.updater }

    final override suspend fun <R> transactional(block: suspend CRUDRepository<T>.() -> R): R =
        client.transactional { block() }!!

    @Suppress("UNCHECKED_CAST")
    final override suspend fun insert(entities: List<T>): Unit = client.transactional {
        client.insert(*entities.withCreatedAtEntities.toTypedArray())
    }!!

    @Suppress("UNCHECKED_CAST")
    final override suspend fun insertAndReturn(entities: List<T>): List<T> = client.transactional {
        client.insertAndReturn(*entities.withCreatedAtEntities.toTypedArray()).toList()
    }!!

    override suspend fun update(entities: List<T>): List<Boolean> = client.transactional {
        entities.map { entity -> update(entity.withUpdatedAt).execute() > 0L }
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

    @Suppress("UNCHECKED_CAST")
    final override suspend fun upsert(entities: List<T>): List<T> = client.transactional {
        val (exists, news) = entities.withIndex().partition { (_, entity) ->
            val id = table.kotysaPk.columns.single().entityGetter(entity)

            id != null && client.selectCount().from(table).predicate(
                table.kotysaPk.columns.single().name.f eq id,
            ).fetchOne()!! > 0
        }

        (exists.onEach { (_, entity) ->
            update(entity.withUpdatedAt).execute()
        } + client.insertAndReturn(*news.map { it.value }.withCreatedAtEntities.toTypedArray())
            .toList()
            .mapIndexed { index, entity ->
               IndexedValue(news[index].index ,entity)
            }).sortedBy { it.index }.map { it.value }
    }!!

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

    override suspend fun delete(predicate: BooleanVariable?): Long = client.transactional {
        predicate?.let {
            client.deleteFrom(table).predicate(it).execute()
        } ?: client.deleteAllFrom(table)
    }!!

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> aggregate(aggregate: AggregateExpression<T>, predicate: BooleanVariable?): T = client.transactional {
        val column = aggregate.projection?.let { table[it.value]!! }

        if (column == null) {
            require(aggregate is Count) {
                "Only in count aggregation column is optional"
            }

            return@transactional client.selectCountFrom(table) as T
        }

        val distinct = aggregate.projection!!.distinct

        when (aggregate) {
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
        }.from(table).apply {
            predicate?.let { predicate(it) }
        }.fetchOne()
    } as T

    private fun update(entity: T): CoroutinesSqlClientDeleteOrUpdate.Return = client.update(table).apply {
        columns.forEach { column -> columnUpdaters[column]!!(this, column.entityGetter(entity)) }
    }.predicate(table.kotysaPk.columns.single().let { it.name.f eq it.entityGetter(entity) })

    private fun update(map: Map<String, Any?>): CoroutinesSqlClientDeleteOrUpdate.Update<T> =
        client.update(table).apply {
            map.entries.forEach { (columnName, value) -> columnUpdaters[table[columnName]!!]!!(this, value) }
        }

    private fun findHelper(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset? = null): Flow<T> =
        client.selectFrom(table).wheres().execute(sort, predicate, limitOffset)

    private fun findHelper(
        projections: List<Variable>,
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset? = null,
    ): Flow<List<Any?>> = client.selects().apply {
        projections.filterIsInstance<Projection>().forEach { projection ->
            val column = table[projection.value]!!

            if (projection.distinct) {
                selectDistinct(column)
            }
            else {
                select(column)
            }.apply {
                projection.alias?.let { `as`(it) }
            }
        }
    }.froms().from(table).wheres().execute(sort, predicate, limitOffset)

    private fun <R : Any> CoroutinesSqlClientSelect.Wheres<R>.execute(
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset?,
    ): Flow<R> = apply {
        predicate?.let { predicate(it) }

        sort?.forEach { order ->
            val column = table[order.name]!!

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
        val isTemporal = table[(expression.arguments[0] as Field).value]!!.isTemporal

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

    private fun Any.kotysaExp(name: String, value: Value<*>): Any = exp(name, value) { table[it]!! }

    private operator fun AbstractTable<T>.get(columnName: String): AbstractDbColumn<T, *>? =
        columns.find { it.name == columnName }
}
