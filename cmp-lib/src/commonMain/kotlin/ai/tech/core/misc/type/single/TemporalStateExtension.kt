@file:OptIn(ExperimentalMaterial3Api::class)

package ai.tech.core.misc.type.single

import androidx.compose.material3.DatePickerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePickerState
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime

public val TimePickerState.localTime: LocalTime
    get() = LocalTime.fromSecondOfDay(hour * 3600 + minute * 60)

public val DatePickerState.localDate: LocalDate?
    get() = selectedDateMillis?.let { LocalDate.fromEpochDays((it / 86400000).toInt()) }

public fun temporalPickerStateToLocalDateTime(
    datePickerState: DatePickerState,
    timePickerState: TimePickerState
): LocalDateTime = LocalDateTime(
    datePickerState.localDate!!,
    timePickerState.localTime
)

public fun temporalPickerStateToTemporal(
    datePickerState: DatePickerState? = null,
    timePickerState: TimePickerState? = null
): Any? = when {
    datePickerState != null && timePickerState != null -> datePickerState.localDate?.let {
        LocalDateTime(
            it,
            timePickerState.localTime
        )
    }

    datePickerState != null -> datePickerState.localDate

    timePickerState != null -> timePickerState.localTime

    else -> throw IllegalStateException("No date or time state provided")
}
