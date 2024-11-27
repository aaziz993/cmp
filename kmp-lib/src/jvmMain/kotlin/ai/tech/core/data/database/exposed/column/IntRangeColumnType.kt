package ai.tech.core.data.database.exposed.column

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.IntegerColumnType
import org.jetbrains.exposed.sql.LongColumnType
import org.jetbrains.exposed.sql.Table

public class IntRangeColumnType : RangeColumnType<Int, IntRange>(IntegerColumnType()) {
    override fun sqlType(): String = "INT4RANGE"

    override fun List<String>.toRange(): IntRange =IntRange(first().toInt(), last().toInt())
}

public fun Table.intRange(name: String): Column<IntRange> = registerColumn(name, IntRangeColumnType())
