package ai.tech.core.misc.datetime.model

import kotlinx.datetime.LocalDateTime
import kotlinx.serialization.Serializable

@Serializable
public data class DateTimeRange(
    override val endInclusive: LocalDateTime,
    override val start: LocalDateTime
) : ClosedRange<LocalDateTime>
