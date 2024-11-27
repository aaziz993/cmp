package ai.tech.core.data.database.exposed.column

import ai.tech.core.misc.datetime.model.DateTimeRange
import ai.tech.core.misc.datetime.model.TimeRange
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalDateTimeColumnType
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalTimeColumnType

public class DateTimeRangeColumnType : RangeColumnType<LocalDateTime, DateTimeRange>(KotlinLocalDateTimeColumnType()) {

    override fun sqlType(): String = "DATETIMERANGE"

    override fun List<String>.toRange(): DateTimeRange = DateTimeRange(LocalDateTime.parse(first()), LocalDateTime.parse(last()))
}

public fun Table.dateTimeRange(name: String): Column<DateTimeRange> = registerColumn(name, DateTimeRangeColumnType())
