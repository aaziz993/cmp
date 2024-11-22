package ai.tech.core.data.database.sql

import ai.tech.core.data.database.sql.model.JoinType
import ai.tech.core.data.database.sql.model.OrderType
import ai.tech.core.data.database.sql.model.SQLDialect

private fun logicalExpression(expression: String, args: List<Any>) = args.joinToString(" $expression ")

public fun and(args: List<Any>): String = logicalExpression("and", args)

public fun or(args: List<Any>): String = logicalExpression("or", args)

public fun not(arg: Any): String = "not $arg"

public fun case(whenThenValues: List<Pair<String, String>>, elseValue: String? = null): String =
    "case\n${
        whenThenValues.joinToString("\n") { (k, v) -> "when $k then $v" }
    }${elseValue?.let { "\nelse $it" }.orEmpty()}\nend"

public fun join(type: JoinType, tableName: String, predicate: String): String = "${type.name.lowercase()} join $tableName on $predicate"

public fun group(columns: List<String>): String = "group by ${columns.joinToString()}"

public fun having(predicate: String): String = "having $predicate"

public fun order(orders: List<Pair<String, OrderType>>): String = "order by ${orders.joinToString() { (k, v) -> "$k $v" }}"

public fun limit(dialect: SQLDialect, offset: Long?, count: Long?): String = when (dialect) {
    SQLDialect.ORACLE -> "${offset?.let { "offset $it rows " }.orEmpty()}${count?.let { "fetch next $it rows only" }.orEmpty()}"

    SQLDialect.FIREBIRD -> "${count?.let { "first $it " }.orEmpty()}${offset?.let { "skip $it" }.orEmpty()}"

    SQLDialect.MYSQL -> "limit ${count ?: "18446744073709551615"}${offset?.let { " offset $it" }.orEmpty()}"

    SQLDialect.POSTGRESQL -> "limit ${count ?: "all"}${offset?.let { " offset $it" }.orEmpty()}"

    SQLDialect.SQLITE -> "limit ${count ?: "-1"}${offset?.let { " offset $it" }.orEmpty()}"

    else -> throw IllegalArgumentException("Unsupported dialect $dialect")
}
