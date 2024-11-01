package ai.tech.core.misc.type.single

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

public fun ZonedDateTime.onSameLocalDay(other: ZonedDateTime): Boolean =
    truncatedTo(ChronoUnit.DAYS) == other.truncatedTo(ChronoUnit.DAYS)

public val ZonedDateTime.isToday: Boolean get() = onSameLocalDay(ZonedDateTime.now())
