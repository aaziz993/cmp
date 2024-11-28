package ai.tech.core.data.database.exposed

import ai.tech.core.data.crud.AbstractCRUDRepository
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
import ai.tech.core.data.expression.EqualsPattern
import ai.tech.core.data.expression.Expression
import ai.tech.core.data.expression.Field
import ai.tech.core.data.expression.GreaterEqualThan
import ai.tech.core.data.expression.GreaterThan
import ai.tech.core.data.expression.In
import ai.tech.core.data.expression.LessEqualThan
import ai.tech.core.data.expression.LessThan
import ai.tech.core.data.expression.Max
import ai.tech.core.data.expression.Min
import ai.tech.core.data.expression.Not
import ai.tech.core.data.expression.NotEquals
import ai.tech.core.data.expression.NotIn
import ai.tech.core.data.expression.Or
import ai.tech.core.data.expression.Projection
import ai.tech.core.data.expression.Sum
import ai.tech.core.data.expression.Value
import ai.tech.core.data.expression.Variable
import ai.tech.core.misc.type.serializablePropertyValues
import ai.tech.core.misc.type.serializer.create
import ai.tech.core.misc.type.single.now
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import net.pearx.kasechange.toCamelCase
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ISqlExpressionBuilder
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.alias
import org.jetbrains.exposed.sql.avg
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalDateColumnType
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalDateTimeColumnType
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalTimeColumnType
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.min
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory

public abstract class AbstractExposedCRUDRepository<T : Any>(
    private val database: Database,
    public val transactionIsolation: Int? = null,
    private val table: Table,
    private val getEntityPropertyValues: (T) -> Map<String, Any?>,
    private val createEntity: (ResultRow) -> T,
    createdAtProperty: String? = "createdAt",
    updatedAtProperty: String? = "updatedAt",
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    private val coroutineContext: CoroutineContext = Dispatchers.IO,
) : AbstractCRUDRepository<T>(
    createdAtProperty,
    updatedAtProperty,
    timeZone,
) {

    @OptIn(InternalSerializationApi::class)
    public constructor(kClass: KClass<T>,
                       database: Database,
                       transactionIsolation: Int? = null,
                       table: Table,
                       createdAtProperty: String? = "createdAt",
                       updatedAtProperty: String? = "updatedAt",
                       timeZone: TimeZone = TimeZone.UTC) : this(
        database,
        transactionIsolation,
        table,
        { it.serializablePropertyValues },
        { resultRow -> Json.Default.create(kClass.serializer(), table.columns.associate { it.name to resultRow[it] }) },
        createdAtProperty,
        updatedAtProperty,
        timeZone,
    )

    init {
        require(table.primaryKey?.columns?.size == 1) {
            "Only table with one identity column primary key is permitted"
        }
    }

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val createdAtColumn = createdAtProperty?.let { table[it]!! }

    override val T.propertyValues: Map<String, Any?>
        get() = getEntityPropertyValues(this)

    override val createdAtNow: ((TimeZone) -> Any)? = createdAtProperty?.let { table[it]!! }?.now

    override val updatedAtNow: ((TimeZone) -> Any)? = createdAtColumn?.now

    private val onUpsertExclude = listOfNotNull(createdAtColumn)

    protected val T.withAt: Map<String, Any?>
        get() = propertyValues.withCreatedAt().withUpdatedAt()

    override suspend fun <R> transactional(block: suspend CRUDRepository<T>.() -> R): R =
        newSuspendedTransaction(coroutineContext, database, transactionIsolation) { block() }

    override suspend fun insert(entities: List<T>): Unit = transactional {
        table.batchInsert(entities.withCreatedAt) { entity -> set(entity) }
    }

    override suspend fun insertAndReturn(entities: List<T>): List<T> = transactional {
        table.batchInsert(entities.withCreatedAt) { entity -> set(entity) }.map(createEntity)
    }

    override suspend fun update(entities: List<T>): List<Boolean> = transactional {
        entities.map { entity -> table.update { it.set(entity.withUpdatedAt) } > 0 }
    }

    override suspend fun update(entities: List<Map<String, Any?>>, predicate: BooleanVariable?): List<Long> = transactional {
        if (predicate == null) {
            entities.map { entity -> table.update { it.set(entity) } }
        }
        else {
            entities.map { entity ->
                table.update({ predicate(predicate) }) {
                    it.set(entity)
                }
            }
        }.map(Int::toLong)
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun upsert(entities: List<T>): List<T> = transactional {
        table.batchUpsert(
            entities,
            onUpdateExclude = onUpsertExclude,
        ) { entity ->
            set(entity.withAt)
        }.map(createEntity)
    }

    override fun find(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset?): Flow<T> = flow {
        transactional {
            table.selectAll().findHelper(sort, predicate, limitOffset).forEach {
                emit(createEntity(it))
            }
        }
    }

    override fun find(projections: List<Variable>, sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset?): Flow<List<Any?>> {
        val columns = projections.filterIsInstance<Projection>().map { projection ->
            table[projection.value]!!.let { column ->
                projection.alias?.let { column.alias(it) } ?: column
            }
        }

        return table.select(columns).findHelper(sort, predicate, limitOffset).map { resultSet ->
            columns.map { resultSet[it] }
        }.asFlow()
    }

    override suspend fun delete(predicate: BooleanVariable?): Long = transactional {
        if (predicate == null) {
            table.deleteAll()
        }
        else {
            table.deleteWhere { it.predicate(predicate) }
        }.toLong()
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T> aggregate(aggregate: AggregateExpression<T>, predicate: BooleanVariable?): T = transactional {

        val column: Column<Comparable<Any>>? = aggregate.projection?.let { table[it.value]!! } as Column<Comparable<Any>>?

        if (column == null) {
            require(aggregate is Count) {
                "Only in count aggregation column is optional"
            }

            return@transactional table.selectAll().predicate(predicate).count() as T
        }

        val distinct = aggregate.projection!!.distinct

        when (aggregate) {
            is Count -> {
                table.select(column.count())
            }

            is Max -> table.select(column.max())

            is Min -> table.select(column.min())

            is Avg -> table.select(column.avg())

            is Sum -> table.select(column.sum())
        }.withDistinct(distinct).predicate(predicate).singleOrNull() as T
    }

    private fun Query.findHelper(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset?) =
        predicate(predicate).apply {
            sort?.forEach {
                orderBy(
                    table[it.name]!!,
                    if (it.ascending) {
                        when (it.nullFirst) {
                            true -> SortOrder.ASC_NULLS_FIRST
                            false -> SortOrder.ASC_NULLS_LAST
                            else -> SortOrder.ASC
                        }
                    }
                    else {
                        when (it.nullFirst) {
                            true -> SortOrder.DESC_NULLS_FIRST
                            false -> SortOrder.DESC_NULLS_LAST
                            else -> SortOrder.DESC
                        }
                    },
                )
            }

            limitOffset?.offset?.let { offset(it) }

            limitOffset?.limit?.let { limit(it.toInt()) }
        }

    private fun Query.predicate(predicate: BooleanVariable?): Query = apply {
        predicate?.let { where { predicate(it) } }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> UpdateBuilder<T>.set(map: Map<String, Any?>) {
        map.forEach { (property, value) ->
            set(table[property] as Column<Any?>, value)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun compareExp(expression: Expression, logValue: StringBuilder): Any {
        val field = expression.arguments[0] as Field

        var exposedExpArgs = listOf(expression.arguments[1] as Value<*>)

        val exposedExpName = when (expression) {
            is Equals -> {
                if (expression.arguments.size > 2) {
                    throw IllegalArgumentException("Unsupported \"${this::class.simpleName}\" with match all or ignore case options")
                }
                "eq"
            }

            is NotEquals -> "neq"

            is EqualsPattern -> {
                val matchAll = (expression.arguments[3] as BooleanValue).value

                if (matchAll) {
                    throw IllegalArgumentException("Unsupported \"${this::class.simpleName}\" with match all option")
                }

                exposedExpArgs = expression.arguments.subList(1, 3) as List<Value<*>>

                "regexp"
            }

            is Between -> "between"

            is GreaterThan -> "grater"

            is GreaterEqualThan -> "greaterEq"

            is LessThan -> "less"

            is LessEqualThan -> "lessEq"

            is In -> "inList"

            is NotIn -> "notInList"

            else -> throw IllegalArgumentException("Unsupported expression type \"${this::class.simpleName}\"")
        }

        logValue.append("${field.value}.$exposedExpName(${exposedExpArgs.joinToString()})")

        return table[field.value]!!.exposedExp(exposedExpName, exposedExpArgs)
    }

    private fun Any.logicExp(expression: Expression, args: List<Any?>, logValue: StringBuilder): Any = when (expression) {
        is And -> "and"
        is Or -> "or"
        is Not -> "not"
        else -> throw IllegalArgumentException("Unsupported expression type \"${this::class.simpleName}\"")
    }.let {
        logValue.append(".$it(${field.value})")
        kotysaExp(it, field)
    }

    @Suppress("UNCHECKED_CAST")
    private fun ISqlExpressionBuilder.predicate(predicate: BooleanVariable): Op<Boolean> {
        val logValue = StringBuilder()

        val value = (predicate as Expression).evaluate(
            { _, args ->
                logicExp(this, ,logValue)
            },
        ) { compareExp(this, logValue) }

        logger.debug("where {}", logValue)

        return value as Op<Boolean>
    }

    private fun Any.exposedExp(name: String, values: List<Value<*>>): Any = exp(name, value) { table[it]!! }
}
