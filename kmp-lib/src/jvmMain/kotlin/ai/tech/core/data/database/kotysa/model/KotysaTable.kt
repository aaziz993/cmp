package ai.tech.core.data.database.kotysa.model

import ai.tech.core.data.expression.Equals
import ai.tech.core.data.expression.f
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import net.pearx.kasechange.toCamelCase
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
import org.ufoss.kotysa.IntColumnNotNull
import org.ufoss.kotysa.IntColumnNullable
import org.ufoss.kotysa.KotlinxLocalDateColumnNotNull
import org.ufoss.kotysa.KotlinxLocalDateColumnNullable
import org.ufoss.kotysa.KotlinxLocalDateTimeColumnNotNull
import org.ufoss.kotysa.KotlinxLocalDateTimeColumnNullable
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
import org.ufoss.kotysa.columns.IntDbIdentityColumnNotNull
import org.ufoss.kotysa.columns.LongDbIdentityColumnNotNull
import org.ufoss.kotysa.columns.UuidDbUuidColumnNotNull
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.typeOf

public class KotysaTable<T : Any>(
    public val table: Table<T>,
    createdAtProperty: String?,
    updatedAtProperty: String?,
) {

    @Suppress("UNCHECKED_CAST")
    public val columns: Map<String, KotysaColumn<T>> =
        table::class.declaredMemberProperties.filter { it.returnType.isSubtypeOf(dbColumnKType) }
            .associate {
                val column = it.call(table)!!
                val columnName =
                    (column::class.memberProperties.find { it.name.equals("columnName", true) }?.call(column)
                        ?.toString()
                        ?: it.name).toCamelCase()
                columnName to KotysaColumn(
                        column as Column<T, *>,
                        column is UuidDbUuidColumnNotNull<*> ||
                                column is IntDbIdentityColumnNotNull<*> ||
                                column is LongDbIdentityColumnNotNull<*>,
                        (column as AbstractDbColumn<T, *>).entityGetter,
                        when (column) {
                            is BigDecimalColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as BigDecimalColumnNotNull<T>).eq(value as BigDecimal)
                                }

                            is BigDecimalColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as BigDecimalColumnNullable<T>).eq(value as BigDecimal?)
                                }

                            is BooleanColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as BooleanColumnNotNull<T>).eq(value as Boolean)
                                }

                            is ByteArrayColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as ByteArrayColumnNotNull<T>).eq(value as ByteArray)
                                }

                            is ByteArrayColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as ByteArrayColumnNullable<T>).eq(value as ByteArray?)
                                }

                            is DoubleColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as DoubleColumnNotNull<T>).eq(value as Double)
                                }

                            is DoubleColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as DoubleColumnNullable<T>).eq(value as Double?)
                                }

                            is FloatColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as FloatColumnNotNull<T>).eq(value as Float)
                                }

                            is FloatColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as FloatColumnNullable<T>).eq(value as Float?)
                                }

                            is IntColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as IntColumnNotNull<T>).eq(value as Int)
                                }

                            is IntColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as IntColumnNullable<T>).eq(value as Int?)
                                }

                            is KotlinxLocalDateColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as KotlinxLocalDateColumnNotNull<T>).eq(value as LocalDate)
                                }

                            is KotlinxLocalDateColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as KotlinxLocalDateColumnNullable<T>).eq(value as LocalDate?)
                                }

                            is KotlinxLocalDateTimeColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as KotlinxLocalDateTimeColumnNotNull<T>).eq(value as LocalDateTime)
                                }

                            is KotlinxLocalDateTimeColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as KotlinxLocalDateTimeColumnNullable<T>).eq(value as LocalDateTime?)
                                }

                            is KotlinxLocalTimeColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as KotlinxLocalTimeColumnNotNull<T>).eq(value as LocalTime)
                                }

                            is KotlinxLocalTimeColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as KotlinxLocalTimeColumnNullable<T>).eq(value as LocalTime?)
                                }

                            is LocalDateColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as LocalDateColumnNotNull<T>).eq(value as java.time.LocalDate)
                                }

                            is LocalDateColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as LocalDateColumnNullable<T>).eq(value as java.time.LocalDate?)
                                }

                            is LocalDateTimeColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as LocalDateTimeColumnNotNull<T>).eq(value as java.time.LocalDateTime)
                                }

                            is LocalDateTimeColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as LocalDateTimeColumnNullable<T>)
                                            .eq(value as java.time.LocalDateTime?)
                                }

                            is LocalTimeColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as LocalTimeColumnNotNull<T>).eq(value as java.time.LocalTime)
                                }

                            is LocalTimeColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as LocalTimeColumnNullable<T>).eq(value as java.time.LocalTime?)
                                }

                            is LongColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as LongColumnNotNull<T>).eq(value as Long)
                                }

                            is LongColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as LongColumnNullable<T>).eq(value as Long?)
                                }

                            is OffsetDateTimeColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as OffsetDateTimeColumnNotNull<T>).eq(value as OffsetDateTime)
                                }

                            is OffsetDateTimeColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as OffsetDateTimeColumnNullable<T>).eq(value as OffsetDateTime?)
                                }

                            is StringColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as StringColumnNotNull<T>).eq(value as String)
                                }

                            is StringColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as StringColumnNullable<T>).eq(value as String?)
                                }

                            is UuidColumnNotNull<*> ->
                                { update, value ->
                                    update.set(column as UuidColumnNotNull<T>).eq(value as UUID)
                                }

                            is UuidColumnNullable<*> ->
                                { update, value ->
                                    update.set(column as UuidColumnNullable<T>).eq(value as UUID?)
                                }

                            else -> throw Exception("No setter defined for column $columnName")
                        },
                )
            }

    public val identityColumn: Map.Entry<String, KotysaColumn<T>> = columns.entries.find { it.value.isIdentity }!!

    public fun getId(entity: T): Any? = identityColumn.value.valueGetter(entity)

    public fun getIdPredicate(entity: T): Equals = identityColumn.key.f eq getId(entity)

    public val createdAtColumn: Map.Entry<String, KotysaColumn<T>>? =
        createdAtProperty?.let { property -> columns.entries.find { it.key == property }!! }

    public val updatedAtColumn: Map.Entry<String, KotysaColumn<T>>? =
        updatedAtProperty?.let { property -> columns.entries.find { it.key == property }!! }

    public operator fun get(key: String): KotysaColumn<T> = columns[key.toCamelCase()]!!

    public companion object {

        private val dbColumnKType = typeOf<DbColumn<*, *>>()
    }
}
