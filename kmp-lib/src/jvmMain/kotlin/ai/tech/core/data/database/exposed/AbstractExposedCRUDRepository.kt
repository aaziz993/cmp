package ai.tech.core.data.database.exposed

import ai.tech.core.data.crud.AbstractCRUDRepository
import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.expression.AggregateExpression
import ai.tech.core.data.expression.Avg
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Count
import ai.tech.core.data.expression.Max
import ai.tech.core.data.expression.Min
import ai.tech.core.data.expression.Projection
import ai.tech.core.data.expression.Sum
import ai.tech.core.data.expression.Variable
import ai.tech.core.misc.type.serializablePropertyValues
import ai.tech.core.misc.type.serializer.new
import ai.tech.core.misc.type.single.now
import kotlin.reflect.KClass
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.datetime.LocalTime
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
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.deleteAll
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.insertIgnore
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalDateColumnType
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalDateTimeColumnType
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalTimeColumnType
import org.jetbrains.exposed.sql.max
import org.jetbrains.exposed.sql.min
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.statements.UpdateBuilder
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.update

public abstract class AbstractExposedCRUDRepository<T : Any>(
    private val database: Database,
    public val transactionIsolation: Int? = null,
    private val table: Table,
    getEntityPropertyValues: (T) -> Map<String, Any?>,
    private val createEntity: (ResultRow) -> T,
    createdAtProperty: String? = "createdAt",
    updatedAtProperty: String? = "updatedAt",
    timeZone: TimeZone = TimeZone.UTC,
) : AbstractCRUDRepository<T>(
    getEntityPropertyValues,
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
        { resultRow -> Json.Default.new(kClass.serializer(), table.columns.associate { it.name to resultRow[it] }) },
        createdAtProperty,
        updatedAtProperty,
        timeZone,
    )

    override val createdAtNow: ((TimeZone) -> Any)? = createdAtProperty?.let { table.columns[it]?.now }

    override val updatedAtNow: ((TimeZone) -> Any)? = updatedAtProperty?.let { table.columns[it]?.now }

    override suspend fun <R> transactional(block: suspend CRUDRepository<T>.() -> R): R =
        newSuspendedTransaction(Dispatchers.IO, database, transactionIsolation) { block() }

    override suspend fun insert(entities: List<T>): Unit = transactional {
        entities.forEach { entity -> table.insert { it.set(entityCreatedAtAware(entity)) } }
    }

    override suspend fun update(entities: List<T>): List<Boolean> = transactional {
        entities.map { entity -> table.update { it.set(entityUpdatedAtAware(entity)) } > 0 }
    }

    override suspend fun update(entities: List<Map<String, Any?>>, predicate: BooleanVariable?): List<Long> = transactional {
        entities.map { entity ->
            table.update(predicate?.let { { predicate(it) } }) {
                it.set(entity)
            }.toLong()
        }
    }

    override fun find(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset?): Flow<T> = flow {
        transactional {
            if (predicate == null) {
                table.selectAll()
            }
            else {
                table.select { predicate(predicate) }
            }.findHelper(sort, predicate, limitOffset).forEach {
                emit(createEntity(it))
            }
        }
    }

    override fun find(projections: List<Variable>, sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset?): Flow<List<Any?>> =
        flow {
            transactional {
                val columns = projections.filterIsInstance<Projection>().map { projection ->
                    table.columns[projection.value]!!.let { column ->
                        projection.alias?.let { column.alias(it) } ?: column
                    }
                }

                table.slice(columns).let {
                    if (predicate == null) {
                        it.selectAll()
                    }
                    else {
                        it.select { predicate(predicate) }
                    }
                }.findHelper(sort, predicate, limitOffset).forEach { resultSet ->
                    emit(columns.map { resultSet[it] })
                }
            }
        }

    private fun Query.findHelper(sort: List<Order>?, predicate: BooleanVariable?, limitOffset: LimitOffset?) =
        apply {

            if (limitOffset != null) {
                limit(limitOffset.limit.toInt(), limitOffset.offset)
            }

            sort?.forEach {
                orderBy(
                    table.columns[it.name]!!,
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
        }

    override suspend fun delete(predicate: BooleanVariable?): Long = transactional {
        if (predicate == null) {
            table.deleteAll().toLong()
        }
        else {
            table.deleteWhere { it.predicate(predicate) }.toLong()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun <T : Comparable<T>> aggregate(aggregate: AggregateExpression<T>, predicate: BooleanVariable?): T = transactional {

        val column = aggregate.projection?.let { table.columns[it.value]!! }

        if (column == null) {
            require(aggregate is Count) {
                "Only in count aggregation column is optional"
            }

            return@transactional table.select(predicate).count() as T
        }

        val distinct = aggregate.projection!!.distinct

        when (aggregate) {
            is Count -> {
                table.slice(column.count())
            }

            is Max -> table.slice(column.max())

            is Min -> table.slice(column.min())

            is Avg -> table.slice(column.avg())

            is Sum -> table.slice(column.sum())
        }.let {
            if (predicate == null) {
                it.selectAll()
            }
            else {
                it.select { predicate(predicate) }
            }
        }.apply {
            if (distinct) {
                withDistinct(true)
            }
        }.singleOrNull() as T
    }

    private fun Table.select(predicate: BooleanVariable?): Query = if (predicate == null) {
        table.selectAll()
    }
    else {
        table.select { predicate(predicate) }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> UpdateBuilder<T>.set(map: Map<String, Any?>) {
        map.forEach { (key, value) ->
            set(table.columns[key] as Column<Any?>, value)
        }
    }

    private fun ISqlExpressionBuilder.predicate(predicate: BooleanVariable): Op<Boolean> {
        val v = (table.columns[0] eq 8)

        v and true
    }
}

public operator fun List<Column<*>>.get(name: String): Column<*>? = find { it.name == name }

public val Column<*>.now: ((TimeZone) -> Any)?
    get() = when (columnType) {
        is KotlinLocalTimeColumnType -> {
            { LocalTime.now(it) }
        }

        is KotlinLocalDateColumnType -> {
            { LocalTime.now(it) }
        }

        is KotlinLocalDateTimeColumnType -> {
            { LocalTime.now(it) }
        }

        else -> null
    }
