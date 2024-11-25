package ai.tech.core.data.database.exposed

import ai.tech.core.data.database.getTables
import ai.tech.core.data.database.model.config.TableConfig
import org.jetbrains.exposed.sql.ForeignKeyConstraint
import org.jetbrains.exposed.sql.Table

public fun getExposedTables(config: TableConfig): List<Table> =
    getTables(Table::class, config) { it.foreignKeys.map(ForeignKeyConstraint::targetTable) }

public fun getExposedTable(tableName: String, configs: List<TableConfig>): Table? =
    configs.flatMap { getExposedTables(it) }.find { it.tableName == tableName }
