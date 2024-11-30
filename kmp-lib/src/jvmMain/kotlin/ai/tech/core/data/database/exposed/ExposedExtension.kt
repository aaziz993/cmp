package ai.tech.core.data.database.exposed

import ai.tech.core.data.database.getTables
import ai.tech.core.data.database.model.config.TableConfig
import ai.tech.core.misc.type.single.now
import kotlin.String
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import net.pearx.kasechange.toCamelCase
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.ForeignKeyConstraint
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalDateColumnType
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalDateTimeColumnType
import org.jetbrains.exposed.sql.kotlin.datetime.KotlinLocalTimeColumnType

internal operator fun Table.get(name: String): Column<*>? = columns.find { it.name.toCamelCase() == name }

internal val Column<*>.now: ((TimeZone) -> Any)?
    get() = when (columnType) {
        is KotlinLocalTimeColumnType -> {
            { LocalTime.now(it) }
        }

        is KotlinLocalDateColumnType -> {
            { LocalTime.now(it) }
        }

        is KotlinLocalDateTimeColumnType -> {
            { LocalTime.now(it) }
        }

        else -> null
    }

internal fun getExposedTables(
    tables: Set<String> = emptySet(),
    scanPackage: String,
    excludePatterns: List<String> = emptyList(),
): List<Table> =
    getTables(
        Table::class,
        tables,
        scanPackage,
        excludePatterns,
    ) { foreignKeys.map(ForeignKeyConstraint::targetTable) }
