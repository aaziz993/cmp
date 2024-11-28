@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package ai.tech.core.data.database.kotysa

import ai.tech.core.data.database.getTables
import ai.tech.core.data.database.model.config.TableConfig
import ai.tech.core.misc.type.single.now
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import net.pearx.kasechange.toCamelCase
import org.ufoss.kotysa.AbstractTable
import org.ufoss.kotysa.BigDecimalColumnNotNull
import org.ufoss.kotysa.BigDecimalColumnNullable
import org.ufoss.kotysa.BooleanColumnNotNull
import org.ufoss.kotysa.ByteArrayColumnNotNull
import org.ufoss.kotysa.ByteArrayColumnNullable
import org.ufoss.kotysa.Column
import org.ufoss.kotysa.CoroutinesSqlClientDeleteOrUpdate
import org.ufoss.kotysa.DoubleColumnNotNull
import org.ufoss.kotysa.DoubleColumnNullable
import org.ufoss.kotysa.FloatColumnNotNull
import org.ufoss.kotysa.FloatColumnNullable
import org.ufoss.kotysa.ForeignKey
import org.ufoss.kotysa.GenericTable
import org.ufoss.kotysa.IntColumnNotNull
import org.ufoss.kotysa.IntColumnNullable
import org.ufoss.kotysa.KotlinxLocalDateColumn
import org.ufoss.kotysa.KotlinxLocalDateColumnNotNull
import org.ufoss.kotysa.KotlinxLocalDateColumnNullable
import org.ufoss.kotysa.KotlinxLocalDateTimeColumn
import org.ufoss.kotysa.KotlinxLocalDateTimeColumnNotNull
import org.ufoss.kotysa.KotlinxLocalDateTimeColumnNullable
import org.ufoss.kotysa.KotlinxLocalTimeColumn
import org.ufoss.kotysa.KotlinxLocalTimeColumnNotNull
import org.ufoss.kotysa.KotlinxLocalTimeColumnNullable
import org.ufoss.kotysa.LocalDateColumnNotNull
import org.ufoss.kotysa.LocalDateColumnNullable
import org.ufoss.kotysa.LocalDateTimeColumnNotNull
import org.ufoss.kotysa.LocalDateTimeColumnNullable
import org.ufoss.kotysa.LocalTimeColumnNotNull
import org.ufoss.kotysa.LocalTimeColumnNullable
import org.ufoss.kotysa.LongColumnNotNull
import org.ufoss.kotysa.LongColumnNullable
import org.ufoss.kotysa.OffsetDateTimeColumnNotNull
import org.ufoss.kotysa.OffsetDateTimeColumnNullable
import org.ufoss.kotysa.PrimaryKey
import org.ufoss.kotysa.StringColumnNotNull
import org.ufoss.kotysa.StringColumnNullable
import org.ufoss.kotysa.Table
import org.ufoss.kotysa.UuidColumnNotNull
import org.ufoss.kotysa.UuidColumnNullable
import org.ufoss.kotysa.columns.AbstractColumn
import org.ufoss.kotysa.columns.AbstractDbColumn
import org.ufoss.kotysa.h2.H2Table
import org.ufoss.kotysa.h2.IH2Table
import org.ufoss.kotysa.mariadb.MariadbTable
import org.ufoss.kotysa.mssql.IMssqlTable
import org.ufoss.kotysa.mssql.MssqlTable
import org.ufoss.kotysa.mysql.MysqlTable
import org.ufoss.kotysa.oracle.OracleTable
import org.ufoss.kotysa.postgresql.IPostgresqlTable
import org.ufoss.kotysa.postgresql.PostgresqlTable

internal val AbstractColumn<*, *>.isTemporal: Boolean
    get() = this is KotlinxLocalTimeColumn<*> || this is KotlinxLocalDateColumn<*> || this is KotlinxLocalDateTimeColumn<*>

internal val AbstractDbColumn<*, *>.now: ((TimeZone) -> Any)?
    get() = when (this) {
        is KotlinxLocalTimeColumn<*> -> {
            { LocalTime.now(it) }
        }

        is KotlinxLocalDateColumn<*>
            -> {
            { LocalDate.now(it) }
        }

        is KotlinxLocalDateTimeColumn<*> -> {
            { LocalDateTime.now(it) }
        }

        else -> null
    }

internal fun <T : Any> ForeignKey<T, *>.referencedTable(tables: List<AbstractTable<T>>): Table<T> = references.entries.first().let { (_, referencedColumn) ->
    tables.single { table -> table.kotysaColumns.any { it === referencedColumn } }
}

@Suppress("UNCHECKED_CAST")
public val <T : Any> AbstractDbColumn<T, *>.updater: CoroutinesSqlClientDeleteOrUpdate.Update<T>.(Any?) -> Unit
    get() = when (this) {
        is BigDecimalColumnNotNull<*> ->
            { value ->
                set(this@updater as BigDecimalColumnNotNull<T>).eq(value as BigDecimal)
            }

        is BigDecimalColumnNullable<*> ->
            { value ->
                set(this@updater as BigDecimalColumnNullable<T>).eq(value as BigDecimal?)
            }

        is BooleanColumnNotNull<*> ->
            { value ->
                set(this@updater as BooleanColumnNotNull<T>).eq(value as Boolean)
            }

        is ByteArrayColumnNotNull<*> ->
            { value ->
                set(this@updater as ByteArrayColumnNotNull<T>).eq(value as ByteArray)
            }

        is ByteArrayColumnNullable<*> ->
            { value ->
                set(this@updater as ByteArrayColumnNullable<T>).eq(value as ByteArray?)
            }

        is DoubleColumnNotNull<*> ->
            { value ->
                set(this@updater as DoubleColumnNotNull<T>).eq(value as Double)
            }

        is DoubleColumnNullable<*> ->
            { value ->
                set(this@updater as DoubleColumnNullable<T>).eq(value as Double?)
            }

        is FloatColumnNotNull<*> ->
            { value ->
                set(this@updater as FloatColumnNotNull<T>).eq(value as Float)
            }

        is FloatColumnNullable<*> ->
            { value ->
                set(this@updater as FloatColumnNullable<T>).eq(value as Float?)
            }

        is IntColumnNotNull<*> ->
            { value ->
                set(this@updater as IntColumnNotNull<T>).eq(value as Int)
            }

        is IntColumnNullable<*> ->
            { value ->
                set(this@updater as IntColumnNullable<T>).eq(value as Int?)
            }

        is KotlinxLocalDateColumnNotNull<*> ->
            { value ->
                set(this@updater as KotlinxLocalDateColumnNotNull<T>).eq(value as LocalDate)
            }

        is KotlinxLocalDateColumnNullable<*> ->
            { value ->
                set(this@updater as KotlinxLocalDateColumnNullable<T>).eq(value as LocalDate?)
            }

        is KotlinxLocalDateTimeColumnNotNull<*> ->
            { value ->
                set(this@updater as KotlinxLocalDateTimeColumnNotNull<T>).eq(value as LocalDateTime)
            }

        is KotlinxLocalDateTimeColumnNullable<*> ->
            { value ->
                set(this@updater as KotlinxLocalDateTimeColumnNullable<T>).eq(value as LocalDateTime?)
            }

        is KotlinxLocalTimeColumnNotNull<*> ->
            { value ->
                set(this@updater as KotlinxLocalTimeColumnNotNull<T>).eq(value as LocalTime)
            }

        is KotlinxLocalTimeColumnNullable<*> ->
            { value ->
                set(this@updater as KotlinxLocalTimeColumnNullable<T>).eq(value as LocalTime?)
            }

        is LocalDateColumnNotNull<*> ->
            { value ->
                set(this@updater as LocalDateColumnNotNull<T>).eq(value as java.time.LocalDate)
            }

        is LocalDateColumnNullable<*> ->
            { value ->
                set(this@updater as LocalDateColumnNullable<T>).eq(value as java.time.LocalDate?)
            }

        is LocalDateTimeColumnNotNull<*> ->
            { value ->
                set(this@updater as LocalDateTimeColumnNotNull<T>).eq(value as java.time.LocalDateTime)
            }

        is LocalDateTimeColumnNullable<*> ->
            { value ->
                set(this@updater as LocalDateTimeColumnNullable<T>)
                    .eq(value as java.time.LocalDateTime?)
            }

        is LocalTimeColumnNotNull<*> ->
            { value ->
                set(this@updater as LocalTimeColumnNotNull<T>).eq(value as java.time.LocalTime)
            }

        is LocalTimeColumnNullable<*> ->
            { value ->
                set(this@updater as LocalTimeColumnNullable<T>).eq(value as java.time.LocalTime?)
            }

        is LongColumnNotNull<*> ->
            { value ->
                set(this@updater as LongColumnNotNull<T>).eq(value as Long)
            }

        is LongColumnNullable<*> ->
            { value ->
                set(this@updater as LongColumnNullable<T>).eq(value as Long?)
            }

        is OffsetDateTimeColumnNotNull<*> ->
            { value ->
                set(this@updater as OffsetDateTimeColumnNotNull<T>).eq(value as OffsetDateTime)
            }

        is OffsetDateTimeColumnNullable<*> ->
            { value ->
                set(this@updater as OffsetDateTimeColumnNullable<T>).eq(value as OffsetDateTime?)
            }

        is StringColumnNotNull<*> ->
            { value ->
                set(this@updater as StringColumnNotNull<T>).eq(value as String)
            }

        is StringColumnNullable<*> ->
            { value ->
                set(this@updater as StringColumnNullable<T>).eq(value as String?)
            }

        is UuidColumnNotNull<*> ->
            { value ->
                set(this@updater as UuidColumnNotNull<T>).eq(value as UUID)
            }

        is UuidColumnNullable<*> ->
            { value ->
                set(this@updater as UuidColumnNullable<T>).eq(value as UUID?)
            }

        else -> throw Exception("No setter defined for this@updater $columnName")
    }

private fun <T : Any> getKotysaTables(
    kClass: KClass<Table<T>>,
    config: TableConfig,
): List<Table<T>> = getTables<Table<T>>(
    kClass,
    config,
) { it.foreignKeys.map { foreignKey -> foreignKey.referencedTable(this) } }

public fun <T : Any> getKotysaH2Tables(config: TableConfig): List<H2Table<T>> =
    getKotysaTables(IH2Table::class, config) + getKotysaTables(
        GenericTable::class,
        config,
    )

public fun getKotysaMariadbTables(config: TableConfig): List<MariadbTable<*>> =
    getKotysaTables(MariadbTable::class, config)

public fun getKotysaMysqlTables(config: TableConfig): List<MysqlTable<*>> =
    getKotysaTables(MysqlTable::class, config)

public fun getKotysaMssqlTables(config: TableConfig): List<IMssqlTable<*>> =
    getKotysaTables(MssqlTable::class, config) + getKotysaTables(
        GenericTable::class,
        config,
    )

public fun getKotysaPostgresqlTables(config: TableConfig): List<IPostgresqlTable<*>> =
    getKotysaTables(PostgresqlTable::class, config) + getKotysaTables(
        GenericTable::class,
        config,
    )

public fun getKotysaOracleTables(config: TableConfig): List<OracleTable<*>> =
    getKotysaTables(OracleTable::class, config)

public fun getKotysaTables(driver: String, configs: List<TableConfig>): List<Table<*>> =
    when (driver) {
        "h2" -> configs.flatMap(::getKotysaH2Tables)

        "postgresql" -> configs.flatMap(::getKotysaPostgresqlTables)

        "mysql" -> configs.flatMap(::getKotysaMysqlTables)

        "mssql" -> configs.flatMap(::getKotysaMssqlTables)

        "mariadb" -> configs.flatMap(::getKotysaMariadbTables)

        "oracle" -> configs.flatMap(::getKotysaOracleTables)

        else -> throw IllegalArgumentException("Unknown database driver \"$driver\"")
    }

public fun getKotysaTable(tableName: String, driver: String, configs: List<TableConfig>): Table<*>? =
    getKotysaTables(driver, configs).find { it.name == tableName }
