package ai.tech.core.data.database.exposed.column

import java.sql.PreparedStatement
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.StringColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.statements.api.PreparedStatementApi
import org.postgresql.util.PGobject

public class LTreeColumnType : StringColumnType() {
    override fun sqlType(): String = "LTREE"

    override fun setParameter(stmt: PreparedStatementApi, index: Int, value: Any?) {
        val parameterValue: PGobject? = value?.let {
            PGobject().apply {
                type = sqlType()
                this.value = value as? String
            }
        }
        super.setParameter(stmt, index, parameterValue)
    }
}

public fun Table.ltree(name: String): Column<String> = registerColumn(name, LTreeColumnType())

