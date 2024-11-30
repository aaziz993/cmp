package ai.tech.core.data.database.exposed

import ai.tech.core.data.database.exposed.column.KotlinDecimalColumnType
import ai.tech.core.data.database.exposed.column.KotlinUuidColumnType
import ai.tech.core.data.database.getTables
import ai.tech.core.misc.type.single.now
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import java.time.OffsetDateTime
import kotlin.String
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import net.pearx.kasechange.toCamelCase
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ForeignKeyConstraint
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalDateColumnType
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalDateTimeColumnType
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalTimeColumnType
import kotlin.reflect.typeOf
import org.jetbrains.exposed.sql.*
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.Instant
import kotlinx.uuid.UUID
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinDurationColumnType
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinInstantColumnType
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinOffsetDateTimeColumnType

internal operator fun Table.get(name: String): Column<*>? = columns.find { it.name.toCamelCase() == name }

internal val Column<*>.now: ((TimeZone) -> Any)?
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

internal val columnKTypes = mapOf(
    typeOf<BooleanColumnType>() to typeOf<Boolean>(),
    typeOf<UByteColumnType>() to typeOf<Byte>(),
    typeOf<UShortColumnType>() to typeOf<Short>(),
    typeOf<UIntegerColumnType>() to typeOf<Int>(),
    typeOf<ULongColumnType>() to typeOf<Long>(),
    typeOf<ByteColumnType>() to typeOf<Byte>(),
    typeOf<ShortColumnType>() to typeOf<Short>(),
    typeOf<IntegerColumnType>() to typeOf<Int>(),
    typeOf<LongColumnType>() to typeOf<Long>(),
    typeOf<FloatColumnType>() to typeOf<Float>(),
    typeOf<DoubleColumnType>() to typeOf<Double>(),
    typeOf<CharColumnType>() to typeOf<Char>(),
    typeOf<StringColumnType>() to typeOf<String>(),
    typeOf<DecimalColumnType>() to typeOf<java.math.BigDecimal>(),
    typeOf<KotlinDecimalColumnType>() to typeOf<BigDecimal>(),
    typeOf<TextColumnType>() to typeOf<String>(),
    typeOf<BinaryColumnType>() to typeOf<ByteArray>(),
    typeOf<UUIDColumnType>() to typeOf<java.util.UUID>(),
    typeOf<KotlinUuidColumnType>() to typeOf<UUID>(),
    typeOf<KotlinOffsetDateTimeColumnType>() to typeOf<OffsetDateTime>(),
    typeOf<KotlinLocalTimeColumnType>() to typeOf<LocalTime>(),
    typeOf<KotlinLocalDateColumnType>() to typeOf<LocalDate>(),
    typeOf<KotlinLocalDateTimeColumnType>() to typeOf<LocalDateTime>(),
    typeOf<KotlinInstantColumnType>() to typeOf<LocalDateTime>(),
    typeOf<KotlinDurationColumnType>() to typeOf<Instant>(),
)

internal fun getExposedTables(
    tables: Set<String> = emptySet(),
    scanPackage: String,
    excludePatterns: List<String> = emptyList(),
): List<Table> =
    getTables(
        Table::class,
        tables,
        scanPackage,
        excludePatterns,
    ) { foreignKeys.map(ForeignKeyConstraint::targetTable) }
