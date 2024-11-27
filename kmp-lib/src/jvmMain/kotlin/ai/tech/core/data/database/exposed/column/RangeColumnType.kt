package ai.tech.core.data.database.exposed.column

import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject

public abstract class RangeColumnType<T : Comparable<T>, R : ClosedRange<T>>(
    public val subType: ColumnType<T>,
) : ColumnType<R>() {
    public abstract fun List<String>.toRange(): R

    override fun nonNullValueToString(value: R): String {
        return "[${value.start},${value.endInclusive}]"
    }

    override fun nonNullValueAsDefaultString(value: R): String {
        return "'${nonNullValueToString(value)}'"
    }

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        val parameterValue: PGobject? = value?.let {
            PGobject().apply {
                type = sqlType()
                this.value = nonNullValueToString(it as R)
            }
        }
        super.setParameter(stmt, index, parameterValue)
    }

    override fun valueFromDB(value: Any): R? = when (value) {
        is PGobject -> value.value?.let {
            val components = it.trim('[', ')').split(',')
            components.toRange()
        }
        else -> error("Retrieved unexpected value of type ${value::class.simpleName}")
    }
}
