package ai.tech.core.misc.datetime.model

import kotlinx.datetime.LocalDate
import kotlinx.serialization.Serializable

@Serializable
public data class DateRange(
    override val endInclusive: LocalDate,
    override val start: LocalDate
) : ClosedRange<LocalDate>
