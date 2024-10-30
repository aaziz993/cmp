package ai.tech.core.misc.type.single

import kotlinx.datetime.DatePeriod
import kotlinx.datetime.DateTimePeriod
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

public val zeroTime: LocalTime = LocalTime.fromSecondOfDay(0)

public fun LocalTime.Companion.parseOrNull(s: String): LocalTime? = s.runCatching { parse(this) }.getOrNull()

public val zeroDate: LocalDate = LocalDate.fromEpochDays(0)

public fun LocalDate.Companion.parseOrNull(s: String): LocalDate? = s.runCatching { parse(this) }.getOrNull()

public fun LocalDate.toEpochMilliseconds(): Long = toEpochDays() * 86400000L

public val zeroDateTime: LocalDateTime = LocalDateTime(zeroDate, zeroTime)

public fun LocalDateTime.Companion.parseOrNull(s: String): LocalDateTime? = s.runCatching { parse(this) }.getOrNull()

public fun DatePeriod.Companion.parseOrNull(s: String): DatePeriod? = s.runCatching { parse(this) }.getOrNull()

public fun DateTimePeriod.Companion.parseOrNull(s: String): DateTimePeriod? = s.runCatching { parse(this) }.getOrNull()
