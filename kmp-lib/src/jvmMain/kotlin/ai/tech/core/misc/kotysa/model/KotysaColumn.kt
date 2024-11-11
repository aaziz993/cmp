package ai.tech.core.misc.kotysa.model

import ai.tech.core.misc.type.single.now
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import org.ufoss.kotysa.BigDecimalColumn
import org.ufoss.kotysa.Column
import org.ufoss.kotysa.CoroutinesSqlClientDeleteOrUpdate
import org.ufoss.kotysa.IntColumn
import org.ufoss.kotysa.KotlinxLocalDateColumn
import org.ufoss.kotysa.KotlinxLocalDateTimeColumn
import org.ufoss.kotysa.KotlinxLocalTimeColumn
import org.ufoss.kotysa.LongColumn
import org.ufoss.kotysa.StringColumn
import org.ufoss.kotysa.UuidColumn

public data class KotysaColumn<T : Any>(
    val column: Column<T, *>,
    val isIdentity: Boolean = false,
    val valueGetter: (T) -> Any?,
    private val valueSetter: (CoroutinesSqlClientDeleteOrUpdate.Update<T>, value: Any?) -> CoroutinesSqlClientDeleteOrUpdate.Update<T>
) {
    public val isInt: Boolean = column is IntColumn

    public val isLong: Boolean = column is LongColumn

    public val isBigDecimal: Boolean = column is BigDecimalColumn

    public val isNumber: Boolean = isInt || isLong || isBigDecimal

    public val isString: Boolean = column is StringColumn

    public val isLocalTime: Boolean = column is KotlinxLocalTimeColumn

    public val isLocalDate: Boolean = column is KotlinxLocalDateColumn

    public val isLocalDateTime: Boolean = column is KotlinxLocalDateTimeColumn

    public val isTemporal: Boolean = isLocalTime || isLocalDate || isLocalDateTime

    public val isUUID: Boolean = column is UuidColumn

    public val now: ((TimeZone) -> Any)? = when {
        isLocalTime -> {
            { LocalTime.now(it) }
        }

        isLocalDate -> {
            { LocalDate.now(it) }
        }

        isLocalDateTime -> {
            { LocalDateTime.now(it) }
        }

        else -> null
    }

    public fun updateFromEntity(
        update: CoroutinesSqlClientDeleteOrUpdate.Update<T>,
        entity: T
    ): CoroutinesSqlClientDeleteOrUpdate.Update<T> =
        valueSetter(update, valueGetter(entity))

    public fun updateFromValue(
        update: CoroutinesSqlClientDeleteOrUpdate.Update<T>,
        value: Any?
    ): CoroutinesSqlClientDeleteOrUpdate.Update<T> =
        valueSetter(update, value)
}
