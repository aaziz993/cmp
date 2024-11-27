package ai.tech.core.data.database.exposed.column

import ai.tech.core.misc.datetime.model.TimeRange
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalTimeColumnType

public class TimeRangeColumnType : RangeColumnType<LocalTime, TimeRange>(KotlinLocalTimeColumnType()) {

    override fun sqlType(): String = "TIMERANGE"

    override fun List<String>.toRange(): TimeRange = TimeRange(LocalTime.parse(first()),  LocalTime.parse(last()))
}

public fun Table.timeRange(name: String): Column<TimeRange> = registerColumn(name, TimeRangeColumnType())
