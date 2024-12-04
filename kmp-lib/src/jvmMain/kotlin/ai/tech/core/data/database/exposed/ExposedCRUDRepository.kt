package ai.tech.core.data.database.exposed

import ai.tech.core.data.crud.AbstractCRUDRepository
import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.query.LimitOffset
import ai.tech.core.data.crud.model.query.Order
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
import ai.tech.core.data.expression.NotEquals
import ai.tech.core.data.expression.NotIn
import ai.tech.core.data.expression.Or
import ai.tech.core.data.expression.Projection
import ai.tech.core.data.expression.Sum
import ai.tech.core.data.expression.Value
import ai.tech.core.data.expression.Variable
import ai.tech.core.data.transaction.Transaction
import ai.tech.core.data.transaction.model.TransactionIsolation
import ai.tech.core.data.transaction.model.javaSqlTransactionIsolation
import ai.tech.core.misc.type.memberProperty
import ai.tech.core.misc.type.serializablePropertyValues
import ai.tech.core.misc.type.serialization.decodeFromAny
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlin.reflect.full.starProjectedType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.TimeZone
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
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
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.min
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transactionManager
import org.jetbrains.exposed.sql.update
import org.slf4j.LoggerFactory

public open class ExposedCRUDRepository<T : Any>(
    private val database: Database,
    private val table: Table,
    private val getEntityPropertyValues: (T) -> Map<String, Any?>,
    private val createEntity: (ResultRow) -> T,
    createdAtProperty: String? = "createdAt",
    updatedAtProperty: String? = "updatedAt",
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    private val coroutineContext: CoroutineContext? = null,
    public val transactionIsolation: TransactionIsolation? = null,
    public val statementCount: Int? = null,
    public val duration: Long? = null,
    public val warnLongQueriesDuration: Long? = null,
    public val debug: Boolean? = null,
    public val maxAttempts: Int? = null,
    public val minRetryDelay: Long? = null,
    public val maxRetryDelay: Long? = null,
    public val queryTimeout: Int? = null,
) : AbstractCRUDRepository<T>(
    createdAtProperty,
    updatedAtProperty,
    timeZone,
) {

    @OptIn(InternalSerializationApi::class)
    public constructor(
        kClass: KClass<T>,
        database: Database,
        table: Table,
        createdAtProperty: String? = "createdAt",
        updatedAtProperty: String? = "updatedAt",
        timeZone: TimeZone = TimeZone.UTC,
        coroutineContext: CoroutineContext? = null,
        transactionIsolation: TransactionIsolation? = null,
        statementCount: Int? = null,
        duration: Long? = null,
        warnLongQueriesDuration: Long? = null,
        debug: Boolean? = null,
        maxAttempts: Int? = null,
        minRetryDelay: Long? = null,
        maxRetryDelay: Long? = null,
        queryTimeout: Int? = null,
    ) : this(
        database,
        table,
        { it.serializablePropertyValues },
        { resultRow -> Json.Default.decodeFromAny(kClass.serializer(), table.columns.associate { it.name to resultRow[it] }) },
        createdAtProperty,
        updatedAtProperty,
        timeZone,
        coroutineContext,
        transactionIsolation,
        statementCount,
        duration,
        warnLongQueriesDuration,
        debug,
        maxAttempts,
        minRetryDelay,
        maxRetryDelay,
        queryTimeout,
    )

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val createdAtColumn = createdAtProperty?.let { table[it]!! }

    final override val T.propertyValues: Map<String, Any?>
        get() = getEntityPropertyValues(this)

    final override val createdAtNow: ((TimeZone) -> Any)? = createdAtProperty?.let { table[it]!! }?.now

    final override val updatedAtNow: ((TimeZone) -> Any)? = createdAtColumn?.now

    private val onUpsertExclude = listOfNotNull(createdAtColumn)

    protected val T.withAt: Map<String, Any?>
        get() = propertyValues.withCreatedAt().withUpdatedAt()

    // CRUD operations in Exposed must be called from within a transaction.
    // By default, a nested transaction block shares the transaction resources of its parent transaction block, so any effect on the child affects the parent
    // Since Exposed 0.16.1 it is possible to use nested transactions as separate transactions by setting useNestedTransactions = true on the desired Database instance.
    final override suspend fun <R> transactional(block: suspend CRUDRepository<T>.(Transaction) -> R): R =
        newSuspendedTransaction(coroutineContext, database, transactionIsolation?.javaSqlTransactionIsolation) {
            this@ExposedCRUDRepository.statementCount?.let { statementCount = it }
            this@ExposedCRUDRepository.duration?.let { duration = it }
            this@ExposedCRUDRepository.warnLongQueriesDuration?.let { warnLongQueriesDuration = it }
            this@ExposedCRUDRepository.debug?.let { debug = it }
            this@ExposedCRUDRepository.maxAttempts?.let { maxAttempts = it }
            this@ExposedCRUDRepository.minRetryDelay?.let { minRetryDelay = it }
            this@ExposedCRUDRepository.maxRetryDelay?.let { maxRetryDelay = it }
            this@ExposedCRUDRepository.queryTimeout?.let { queryTimeout = it }
            block(ExposedTransaction(this))
        }

    private suspend fun <R> internalTransactional(block: suspend () -> R): R =
        if (database.transactionManager.currentOrNull() == null) {
            transactional { block() }
        }
        else {
            block()
        }

    final override suspend fun insert(entities: List<T>): Unit = internalTransactional {

        table.batchInsert(entities.withCreatedAt) { entity -> set(entity) }
    }

    final override suspend fun insertAndReturn(entities: List<T>): List<T> = internalTransactional {
        table.batchInsert(entities.withCreatedAt) { entity -> set(entity) }.map(createEntity)
    }

    final override suspend fun update(entities: List<T>): List<Boolean> = internalTransactional {
        entities.map { entity -> table.update { it.set(entity.withUpdatedAt) } > 0 }
    }

    final override suspend fun update(propertyValues: List<Map<String, Any?>>, predicate: BooleanVariable?): Long = internalTransactional {
        if (predicate == null) {
            propertyValues.map { entity -> table.update { it.set(entity) } }
        }
        else {
            propertyValues.map { entity ->
                table.update({ predicate(predicate) }) {
                    it.set(entity)
                }
            }
        }.first().let(Int::toLong)
    }

    @Suppress("UNCHECKED_CAST")
    final override suspend fun upsert(entities: List<T>): List<T> = internalTransactional {
        table.batchUpsert(
            entities,
            onUpdateExclude = onUpsertExclude,
        ) { entity ->
            set(entity.withAt)
        }.map(createEntity)
    }

    final override fun find(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset?): Flow<T> = flow {
        internalTransactional {
            table.selectAll().findHelper(sort, predicate, limitOffset).forEach {
                emit(createEntity(it))
            }
        }
    }

    final override fun find(projections: List<Variable>, sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset?): Flow<List<Any?>> = flow {
        internalTransactional {
            val columns = projections.filterIsInstance<Projection>().map { projection ->
                table[projection.value]!!.let { column ->
                    projection.alias?.let { column.alias(it) } ?: column
                }
            }

            table.select(columns).findHelper(sort, predicate, limitOffset).forEach { resultSet ->
                emit(columns.map { resultSet[it] })
            }
        }
    }

    final override suspend fun delete(predicate: BooleanVariable?): Long = internalTransactional {
        if (predicate == null) {
            table.deleteAll()
        }
        else {
            table.deleteWhere { it.predicate(predicate) }
        }.toLong()
    }

    @Suppress("UNCHECKED_CAST")
    final override suspend fun <T> aggregate(aggregate: AggregateExpression<T>, predicate: BooleanVariable?): T = internalTransactional {

        val column: Column<Comparable<Any>>? = aggregate.projection?.let { table[it.value]!! } as Column<Comparable<Any>>?

        if (column == null) {
            require(aggregate is Count) {
                "Only in count aggregation column is optional"
            }

            return@internalTransactional table.selectAll().predicate(predicate).count() as T
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
    private fun Any.eval(expression: Expression, args: List<Any?>): Any {

        var funArgs = args

        val funName = when (expression) {
            is And -> {}

            is Or -> {}

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

                funArgs = args.dropLast(1)

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

        val columnValueKType: KType? = null

        args.map { arg ->
            when (arg) {
                is Field -> {
                    table[arg.value]!!.let { it to it::class.starProjectedType }
                }

                is Value<*> -> arg.value to arg::class.memberProperty("value")!!.returnType
                else -> arg to (arg?.let { it::class.starProjectedType } ?: columnValueKType)!!
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun ISqlExpressionBuilder.predicate(predicate: BooleanVariable): Op<Boolean> =
        (predicate as Expression).map(
            { expression, args -> eval(expression, args) },
        ) { expression -> eval(expression, expression.arguments) } as Op<Boolean>
}

public fun <T : Any> Database.repository(
    table: Table,
    getEntityPropertyValues: (T) -> Map<String, Any?>,
    createEntity: (ResultRow) -> T,
    createdAtProperty: String? = "createdAt",
    updatedAtProperty: String? = "updatedAt",
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    coroutineContext: CoroutineContext? = null,
    transactionIsolation: TransactionIsolation? = null,
    statementCount: Int? = null,
    duration: Long? = null,
    warnLongQueriesDuration: Long? = null,
    debug: Boolean? = null,
    maxAttempts: Int? = null,
    minRetryDelay: Long? = null,
    maxRetryDelay: Long? = null,
    queryTimeout: Int? = null,
): CRUDRepository<T> = ExposedCRUDRepository(
    this,
    table,
    getEntityPropertyValues,
    createEntity,
    createdAtProperty,
    updatedAtProperty,
    timeZone,
    coroutineContext,
    transactionIsolation,
    statementCount,
    duration,
    warnLongQueriesDuration,
    debug,
    maxAttempts,
    minRetryDelay,
    maxRetryDelay,
    queryTimeout,
)

public fun <T : Any> Database.repository(
    kClass: KClass<T>,
    table: Table,
    createdAtProperty: String? = "createdAt",
    updatedAtProperty: String? = "updatedAt",
    timeZone: TimeZone = TimeZone.UTC,
    coroutineContext: CoroutineContext? = null,
    transactionIsolation: TransactionIsolation? = null,
    statementCount: Int? = null,
    duration: Long? = null,
    warnLongQueriesDuration: Long? = null,
    debug: Boolean? = null,
    maxAttempts: Int? = null,
    minRetryDelay: Long? = null,
    maxRetryDelay: Long? = null,
    queryTimeout: Int? = null,
): CRUDRepository<T> = ExposedCRUDRepository(
    kClass,
    this,
    table,
    createdAtProperty,
    updatedAtProperty,
    timeZone,
    coroutineContext,
    transactionIsolation,
    statementCount,
    duration,
    warnLongQueriesDuration,
    debug,
    maxAttempts,
    minRetryDelay,
    maxRetryDelay,
    queryTimeout,
)

