@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package ai.tech.core.data.database.kotysa

import ai.tech.core.data.crud.AbstractTypeCRUDRepository
import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.query.LimitOffset
import ai.tech.core.data.crud.model.query.Order
import ai.tech.core.data.database.r2dbc.R2dbcConnectionFactory
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
import ai.tech.core.data.transaction.Transaction
import ai.tech.core.data.transaction.model.TransactionIsolation
import ai.tech.core.data.transaction.model.r2dbcTransactionIsolation
import ai.tech.core.misc.type.invoke
import ai.tech.core.misc.type.multiple.toTypedArray
import ai.tech.core.misc.type.serialization.decodeFromAny
import ai.tech.core.misc.type.serializer.create
import io.r2dbc.spi.Connection
import java.lang.reflect.UndeclaredThrowableException
import java.sql.SQLException
import kotlin.coroutines.coroutineContext
import kotlin.reflect.KClass
import kotlin.reflect.full.starProjectedType
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import kotlinx.coroutines.withContext
import kotlinx.datetime.TimeZone
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import org.slf4j.LoggerFactory
import org.ufoss.kotysa.AbstractTable
import org.ufoss.kotysa.CoroutinesSqlClientDeleteOrUpdate
import org.ufoss.kotysa.CoroutinesSqlClientSelect
import org.ufoss.kotysa.KotlinxLocalDateColumn
import org.ufoss.kotysa.KotlinxLocalDateTimeColumn
import org.ufoss.kotysa.KotlinxLocalTimeColumn
import org.ufoss.kotysa.MinMaxColumn
import org.ufoss.kotysa.NumericColumn
import org.ufoss.kotysa.R2dbcSqlClient
import org.ufoss.kotysa.SqlClientQuery
import org.ufoss.kotysa.WholeNumberColumn
import org.ufoss.kotysa.columns.AbstractDbColumn
import org.ufoss.kotysa.core.r2dbc.transaction.R2dbcTransaction
import org.ufoss.kotysa.r2dbc.transaction.R2dbcTransactionImpl
import org.ufoss.kotysa.r2dbc.SqlClientR2dbc

@OptIn(InternalSerializationApi::class)
public abstract class AbstractKotysaCRUDRepository<T : Any>(
    public val client: R2dbcSqlClient,
    public val table: AbstractTable<T>,
    createEntity: Map<String, Any?>. () -> T,
    createdAtProperty: String? = "createdAt",
    updatedAtProperty: String? = "updatedAt",
    timeZone: TimeZone = TimeZone.currentSystemDefault(),
    public val transactionIsolation: TransactionIsolation? = null,
    private val autoCommit: Boolean? = null,
    private val statementTimeout: Duration? = null,
    private val lockWaitTimeout: Duration? = null,
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
        { Json.Default.decodeFromAny(kClass.serializer(), this) },
        createdAtProperty,
        updatedAtProperty,
        timeZone,
    )

    private val logger = LoggerFactory.getLogger(this::class.java)

    private val columns = table.kotysaColumns.filterIsInstance<AbstractDbColumn<T, *>>()

    override val T.propertyValues: Map<String, Any?>
        get() = columns.associate { column -> column.name to column.entityGetter(this) }

    override val createdAtNow: ((TimeZone) -> Any)? = createdAtProperty?.let { property -> table[property]!! }?.now

    override val updatedAtNow: ((TimeZone) -> Any)? = updatedAtProperty?.let { property -> table[property]!! }?.now

    final override suspend fun <R> transactional(block: suspend CRUDRepository<T>.(Transaction) -> R): R =
        transactionalProtected {
            block(
                KotysaTransaction(
                    (it as R2dbcTransactionImpl).also {
                        it.connection.apply {
                            transactionIsolation?.let { setTransactionIsolationLevel(it.r2dbcTransactionIsolation).awaitSingle() }
                            autoCommit?.let { setAutoCommit(it).awaitSingle() }
                            statementTimeout?.let { setStatementTimeout(it.toJavaDuration()).awaitSingle() }
                            lockWaitTimeout?.let { setLockWaitTimeout(it.toJavaDuration()).awaitSingle() }
                        }
                    },
                ),
            )
        }!!

    @Suppress("UNCHECKED_CAST")
    final override suspend fun insert(entities: List<T>): Unit =
        client.insert(*entities.withCreatedAtEntities.toTypedArray())

    @Suppress("UNCHECKED_CAST")
    final override suspend fun insertAndReturn(entities: List<T>): List<T> =
        client.insertAndReturn(*entities.withCreatedAtEntities.toTypedArray()).toList()

    final override suspend fun update(entities: List<T>): List<Boolean> =
        entities.map { entity -> update(entity.withUpdatedAtEntity).execute() > 0L }

    final override suspend fun update(
        propertyValues: List<Map<String, Any?>>,
        predicate: BooleanVariable?,
    ): Long =
        if (predicate == null) {
            propertyValues.map { update(it).execute() }
        }
        else {
            propertyValues.map { update(it).predicate(predicate).execute() }
        }.first()

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
                IndexedValue(news[index].index, entity)
            }).sortedBy(IndexedValue<T>::index).map(IndexedValue<T>::value)
    }!!

    final override fun find(
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset?
    ): Flow<T> = findHelper(sort, predicate, limitOffset)

    final override fun find(
        projections: List<Variable>,
        sort: List<Order>?,
        predicate: BooleanVariable?,
        limitOffset: LimitOffset?
    ): Flow<List<Any?>> = findHelper(projections, sort, predicate, limitOffset)

    final override suspend fun delete(predicate: BooleanVariable?): Long =
        predicate?.let {
            client.deleteFrom(table).predicate(it).execute()
        } ?: client.deleteAllFrom(table)

    @Suppress("UNCHECKED_CAST")
    final override suspend fun <T> aggregate(aggregate: AggregateExpression<T>, predicate: BooleanVariable?): T {
        val column = aggregate.projection?.let { table[it.value]!! }

        if (column == null) {
            require(aggregate is Count) {
                "Only in count aggregation column is optional"
            }

            return client.selectCountFrom(table) as T
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
        }.from(table).apply {
            predicate?.let { predicate(it) }
        }.fetchOne() as T
    }

    private fun update(entity: T): CoroutinesSqlClientDeleteOrUpdate.Return = client.update(table).apply {
        columns.forEach { column -> set(column, column.entityGetter(entity)) }
    }.predicate(table.kotysaPk.columns.single().let { it.name.f eq it.entityGetter(entity) })

    private fun update(map: Map<String, Any?>): CoroutinesSqlClientDeleteOrUpdate.Update<T> =
        client.update(table).apply {
            map.entries.forEach { (columnName, value) -> set(table[columnName]!!, value) }
        }

    private fun CoroutinesSqlClientDeleteOrUpdate.Update<T>.set(column: AbstractDbColumn<T, *>, value: Any?) {
        this("set", column to column::class.starProjectedType)!!("eq", value to column::class.starProjectedType.valueKType)
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

        (predicate as Expression).depthMap(
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
        val isTemporal = table[(expression.arguments[0] as Field).value]!!.this is KotlinxLocalTimeColumn<*> || this is KotlinxLocalDateColumn<*> || this is KotlinxLocalDateTimeColumn<*>

        return if (expression is Between) {
            if (isTemporal) {
                this("afterOrEq", expression.arguments[1] as Value<*>)
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

    protected suspend fun <T> transactionalProtected(block: suspend (R2dbcTransaction) -> T): T? {
        val connectionFactory = (client as SqlClientR2dbc).connectionFactory as R2dbcConnectionFactory

        // reuse currentTransaction if any or create nested transaction if required and supported, else create new transaction from new established connection
        val currentTransaction = coroutineContext[R2dbcTransactionImpl]
        val isOrigin = currentTransaction == null
        var context = coroutineContext
        val transaction = if (currentTransaction != null && !currentTransaction.isCompleted()) {
            currentTransaction
        }
        else {
            // if new transaction : add it to coroutineContext
            R2dbcTransactionImpl((connectionFactory.create().awaitSingle()).also { context += it }
        }
        var throwable: Throwable? = null

        // use transaction's Connection
        return with(transaction.connection) {
            setAutoCommit(false).awaitFirstOrNull() // default true

            try {
                val result = try {
                    withContext(context) {
                        block.invoke(transaction)
                    }
                }
                catch (ex: SQLException) { // An expected checked Exception in JDBC
                    throwable = ex
                    throw ex
                }
                catch (ex: RuntimeException) {
                    throwable = ex
                    throw ex
                }
                catch (ex: Error) {
                    throwable = ex
                    throw ex
                }
                catch (ex: Throwable) {
                    // Transactional block threw unexpected exception
                    throwable = ex
                    throw UndeclaredThrowableException(ex, "block threw undeclared checked exception")
                }

                result
            }
            finally {
                // For original transaction only : commit or rollback, then close connection
                if (isOrigin) {
                    try {
                        if (throwable != null) {
                            rollbackTransaction().awaitFirstOrNull()
                        }
                        else {
                            commitTransaction().awaitFirstOrNull()
                        }
                    }
                    finally {
                        try {
                            transaction.setCompleted()
                            close().awaitFirstOrNull()
                        }
                        catch (_: Throwable) {
                            // ignore exception of connection.close()
                        }
                    }
                }
            }
        }
    }

    private operator fun AbstractTable<T>.get(columnName: String): AbstractDbColumn<T, *>? =
        columns.find { it.name == columnName }
}
