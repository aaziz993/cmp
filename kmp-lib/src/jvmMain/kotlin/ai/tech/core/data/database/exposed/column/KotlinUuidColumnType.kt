@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package ai.tech.core.data.database.exposed.column

import java.nio.ByteBuffer
import java.sql.ResultSet
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnType
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.vendors.MariaDBDialect
import org.jetbrains.exposed.sql.vendors.currentDialect

@OptIn(ExperimentalUuidApi::class)
public class KotlinUuidColumnType : ColumnType<Uuid>() {

    override fun sqlType(): String = currentDialect.dataTypeProvider.uuidType()

    override fun valueFromDB(value: Any): Uuid = when {
        value is Uuid -> value
        value is ByteArray -> ByteBuffer.wrap(value).let { b -> Uuid.fromLongs(b.long, b.long) }
        value is String && value.matches(uuidRegexp) -> Uuid.parse(value)
        value is String -> ByteBuffer.wrap(value.toByteArray()).let { b -> Uuid.fromLongs(b.long, b.long) }
        else -> error("Unexpected value of type Uuid: $value of ${value::class.qualifiedName}")
    }

    override fun notNullValueToDB(value: Uuid): Any = ByteBuffer.allocate(16).putLong(value.mostSignificantBits).putLong(value.leastSignificantBits).array()

    override fun nonNullValueToString(value: Uuid): String = "'$value'"

    override fun readObject(rs: ResultSet, index: Int): Any? = when (currentDialect) {
        is MariaDBDialect -> rs.getBytes(index)
        else -> super.readObject(rs, index)
    }

    public companion object {

        private val uuidRegexp =
            Regex("[0-9A-F]{8}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{4}-[0-9A-F]{12}", RegexOption.IGNORE_CASE)
    }
}

@OptIn(ExperimentalUuidApi::class)
public fun Table.kotlinUuid(name: String): Column<Uuid> = registerColumn(name, KotlinUuidColumnType())
