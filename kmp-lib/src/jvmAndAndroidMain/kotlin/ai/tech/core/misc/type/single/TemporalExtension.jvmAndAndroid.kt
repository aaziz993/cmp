package ai.tech.core.misc.type.single

import io.ktor.util.date.*
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.util.*
import java.util.concurrent.TimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.datetime.toJavaInstant

public val GreenwichMeanTime: ZoneId = ZoneId.of("GMT")

public fun Instant.toGMTDate(): GMTDate =
    GMTDate(TimeUnit.SECONDS.toMillis(atZone(ZoneOffset.UTC).toEpochSecond()))

public fun LocalDate.toJavaInstant(timeZone: TimeZone) = toInstant(timeZone).toJavaInstant()

public fun LocalDate.toJavaDateOrNull(timeZone: TimeZone): Date? = Date.from(toJavaInstant(timeZone))

public fun LocalDate.toJavaDate(timeZone: TimeZone): Date? = toJavaDate(timeZone)!!

public fun LocalDateTime.toJavaInstant(timeZone: TimeZone) = toInstant(timeZone).toJavaInstant()

public fun LocalDateTime.toJavaDate(timeZone: TimeZone): Date? = Date.from(toJavaInstant(timeZone))

public fun ZonedDateTime.onSameLocalDay(other: ZonedDateTime): Boolean =
    truncatedTo(ChronoUnit.DAYS) == other.truncatedTo(ChronoUnit.DAYS)

public val ZonedDateTime.isToday: Boolean get() = onSameLocalDay(ZonedDateTime.now())

public fun ZonedDateTime.toGMTDate(): GMTDate = toInstant().toGMTDate()

public fun Date.toLocalDate(): java.time.LocalDate? = java.time.LocalDate.ofInstant(toInstant(), ZoneId.systemDefault())

public fun Date.toLocalTime(): java.time.LocalTime? = java.time.LocalTime.ofInstant(toInstant(), ZoneId.systemDefault())

public fun Date.toLocalDateTime(): java.time.LocalDateTime = java.time.LocalDateTime.ofInstant(toInstant(), ZoneId.systemDefault())

public fun Date.toZonedDateTime(): ZonedDateTime = ZonedDateTime.ofInstant(toInstant(), GreenwichMeanTime)
