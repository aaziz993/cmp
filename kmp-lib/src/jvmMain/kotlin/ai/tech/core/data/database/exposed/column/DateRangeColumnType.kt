package ai.tech.core.data.database.exposed.column

import ai.tech.core.misc.datetime.model.DateRange
import kotlinx.datetime.LocalDate
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalDateColumnType

public class DateRangeColumnType : RangeColumnType<LocalDate, DateRange>(KotlinLocalDateColumnType()) {

    override fun sqlType(): String = "DATERANGE"

    override fun List<String>.toRange(): DateRange =DateRange(LocalDate.parse(first()),  LocalDate.parse(last()))
}

public fun Table.dateRange(name: String): Column<DateRange> = registerColumn(name, DateRangeColumnType())
