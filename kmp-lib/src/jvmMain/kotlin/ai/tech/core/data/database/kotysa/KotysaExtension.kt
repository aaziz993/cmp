@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package ai.tech.core.data.database.kotysa

import ai.tech.core.data.database.getTables
import ai.tech.core.misc.type.single.now
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.typeOf
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import net.sourceforge.jeuclid.elements.presentation.table.AbstractTableRow
import org.jetbrains.kotlinx.dataframe.io.db.DbType
import org.ufoss.kotysa.AbstractTable
import org.ufoss.kotysa.BigDecimalColumnNotNull
import org.ufoss.kotysa.BigDecimalColumnNullable
import org.ufoss.kotysa.BooleanColumnNotNull
import org.ufoss.kotysa.ByteArrayColumnNotNull
import org.ufoss.kotysa.ByteArrayColumnNullable
import org.ufoss.kotysa.Column
import org.ufoss.kotysa.DbColumn
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
import org.ufoss.kotysa.StringColumnNotNull
import org.ufoss.kotysa.StringColumnNullable
import org.ufoss.kotysa.Table
import org.ufoss.kotysa.UuidColumnNotNull
import org.ufoss.kotysa.UuidColumnNullable
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

internal fun <T : AbstractTable<*>> ForeignKey<*, *>.referencedTable(tables: List<T>): T = references.entries.first().let { (_, referencedColumn) ->
    tables.single { table -> table.kotysaColumns.any { it === referencedColumn } }
}

internal val columnValueKTypes = mapOf(
    BigDecimalColumnNotNull::class to typeOf<BigDecimal>(),
    BigDecimalColumnNullable::class to typeOf<BigDecimal?>(),
    BooleanColumnNotNull::class to typeOf<Boolean>(),
    ByteArrayColumnNotNull::class to typeOf<ByteArray>(),
    ByteArrayColumnNullable::class to typeOf<ByteArray?>(),
    DoubleColumnNotNull::class to typeOf<Double>(),
    DoubleColumnNullable::class to typeOf<Double?>(),
    FloatColumnNotNull::class to typeOf<Float>(),
    FloatColumnNullable::class to typeOf<Float?>(),
    IntColumnNotNull::class to typeOf<Int>(),
    IntColumnNullable::class to typeOf<Int?>(),
    KotlinxLocalDateColumnNotNull::class to typeOf<LocalDate>(),
    KotlinxLocalDateColumnNullable::class to typeOf<LocalDate?>(),
    KotlinxLocalDateTimeColumnNotNull::class to typeOf<LocalDateTime>(),
    KotlinxLocalDateTimeColumnNullable::class to typeOf<LocalDateTime?>(),
    KotlinxLocalTimeColumnNotNull::class to typeOf<LocalTime>(),
    KotlinxLocalTimeColumnNullable::class to typeOf<LocalTime?>(),
    LocalDateColumnNotNull::class to typeOf<java.time.LocalDate>(),
    LocalDateColumnNullable::class to typeOf<java.time.LocalDate?>(),
    LocalDateTimeColumnNotNull::class to typeOf<java.time.LocalDateTime>(),
    LocalDateTimeColumnNullable::class to typeOf<java.time.LocalDateTime?>(),
    LocalTimeColumnNotNull::class to typeOf<java.time.LocalTime>(),
    LocalTimeColumnNullable::class to typeOf<java.time.LocalTime?>(),
    LongColumnNotNull::class to typeOf<Long>(),
    LongColumnNullable::class to typeOf<Long?>(),
    OffsetDateTimeColumnNotNull::class to typeOf<OffsetDateTime>(),
    OffsetDateTimeColumnNullable::class to typeOf<OffsetDateTime?>(),
    StringColumnNotNull::class to typeOf<String>(),
    StringColumnNullable::class to typeOf<String?>(),
    UuidColumnNotNull::class to typeOf<UUID>(),
    UuidColumnNullable::class to typeOf<UUID?>(),
)

internal val <T : DbColumn<*, *>> KClass<T>.valueKType
    get() = columnValueKTypes[this]

private fun <T : AbstractTable<*>> getKotysaTables(
    kClass: KClass<T>,
    packages: Set<String>,
    names: Set<String> = emptySet(),
    inclusive: Boolean = false,
): List<T> = getTables(
    kClass,
    packages,
    names,
    inclusive,
) { tables -> kotysaForeignKeys.map { foreignKey -> foreignKey.referencedTable(tables) } }

internal fun getKotysaH2Tables(
    packages: Set<String>,
    names: Set<String> = emptySet(),
    inclusive: Boolean = false,
): List<IH2Table<*>> =
    getKotysaTables(
        H2Table::class,
        packages,
        names,
        inclusive,
    ) + getKotysaTables(
        GenericTable::class,
        packages,
        names,
        inclusive,
    )

internal fun getKotysaMariadbTables(
    packages: Set<String>,
    names: Set<String> = emptySet(),
    inclusive: Boolean = false,
): List<MariadbTable<*>> =
    getKotysaTables(
        MariadbTable::class,
        packages,
        names,
        inclusive,
    )

internal fun getKotysaMysqlTables(
    packages: Set<String>,
    names: Set<String> = emptySet(),
    inclusive: Boolean = false,
): List<MysqlTable<*>> =
    getKotysaTables(
        MysqlTable::class,
        packages,
        names,
        inclusive,
    )

internal fun getKotysaMssqlTables(
    packages: Set<String>,
    names: Set<String> = emptySet(),
    inclusive: Boolean = false,
): List<IMssqlTable<*>> =
    getKotysaTables(
        MssqlTable::class,
        packages,
        names,
        inclusive,
    ) + getKotysaTables(
        GenericTable::class,
        packages,
        names,
        inclusive,
    )

internal fun getKotysaPostgresqlTables(
    packages: Set<String>,
    names: Set<String> = emptySet(),
    inclusive: Boolean = false,
): List<IPostgresqlTable<*>> =
    getKotysaTables(
        PostgresqlTable::class,
        packages,
        names,
        inclusive,
    ) + getKotysaTables(
        GenericTable::class,
        packages,
        names,
        inclusive,
    )

internal fun getKotysaOracleTables(
    packages: Set<String>,
    names: Set<String> = emptySet(),
    inclusive: Boolean = false,
): List<OracleTable<*>> =
    getKotysaTables(
        OracleTable::class,
        packages,
        names,
        inclusive,
    )

@Suppress("UNCHECKED_CAST")
internal fun getKotysaTables(
    driver: String,
    packages: Set<String>,
    names: Set<String> = emptySet(),
    inclusive: Boolean = false,
): List<AbstractTable<*>> =
    when (driver) {
        "h2" -> getKotysaH2Tables(
            packages,
            names,
            inclusive,
        ) as List<AbstractTable<*>>

        "postgresql" -> getKotysaPostgresqlTables(
            packages,
            names,
            inclusive,
        ) as List<AbstractTable<*>>

        "mysql" -> getKotysaMysqlTables(
            packages,
            names,
            inclusive,
        )

        "mssql" -> getKotysaMssqlTables(
            packages,
            names,
            inclusive,
        ) as List<AbstractTable<*>>

        "mariadb" -> getKotysaMariadbTables(
            packages,
            names,
            inclusive,
        )

        "oracle" -> getKotysaOracleTables(
            packages,
            names,
            inclusive,
        )

        else -> throw IllegalArgumentException("Unknown database driver \"$driver\"")
    }
