package ai.tech.core.misc.datetime.model

import kotlinx.datetime.LocalTime
import kotlinx.serialization.Serializable

@Serializable
public data class TimeRange(
    override val endInclusive: LocalTime,
    override val start: LocalTime
) : ClosedRange<LocalTime>
