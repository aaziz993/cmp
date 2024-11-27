package ai.tech.core.data.database.exposed.column

import kotlin.reflect.KClass
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ColumnTransformer
import org.jetbrains.exposed.sql.StringColumnType
import org.jetbrains.exposed.sql.Table

public class SetColumnType<T : Enum<T>>(
    private val enumClass: KClass<T>
) : StringColumnType() {
    // uses reflection to retrieve elements of the enum class
    private val enumConstants by lazy {
        enumClass.java.enumConstants?.map { it.name } ?: emptyList()
    }

    override fun sqlType(): String = enumConstants
        .takeUnless { it.isEmpty() }
        ?.let { "SET(${it.joinToString { e -> "'$e'" }})" }
        ?: error("SET column must be defined with a list of permitted values")
}

public inline fun <reified T : Enum<T>> Table.set(name: String): Column<String> =
    registerColumn(name, SetColumnType(T::class))
