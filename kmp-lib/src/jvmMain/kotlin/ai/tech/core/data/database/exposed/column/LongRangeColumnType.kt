package ai.tech.core.data.database.exposed.column

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.Table

public class LongRangeColumnType : RangeColumnType<Long, LongRange>(LongColumnType()) {
    override fun sqlType(): String = "INT8RANGE"

    override fun List<String>.toRange(): LongRange =LongRange(first().toLong(), last().toLong())
}

public fun Table.longRange(name: String): Column<LongRange> = registerColumn(name, LongRangeColumnType())
