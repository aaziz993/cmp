package ai.tech.core.data.database.kotysa

import ai.tech.core.data.database.getTables
import ai.tech.core.data.database.model.config.TableConfig
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import org.ufoss.kotysa.ForeignKey
import org.ufoss.kotysa.GenericTable
import org.ufoss.kotysa.Table
import org.ufoss.kotysa.columns.AbstractColumn
import org.ufoss.kotysa.columns.AbstractDbColumn
import org.ufoss.kotysa.h2.IH2Table
import org.ufoss.kotysa.mariadb.MariadbTable
import org.ufoss.kotysa.mssql.IMssqlTable
import org.ufoss.kotysa.mssql.MssqlTable
import org.ufoss.kotysa.mysql.MysqlTable
import org.ufoss.kotysa.oracle.OracleTable
import org.ufoss.kotysa.postgresql.IPostgresqlTable
import org.ufoss.kotysa.postgresql.PostgresqlTable

public val Table<*>.name: String
    get() = this::class.declaredMemberProperties.find { it.name == "tableName" }!!.getter.call(this)!!.toString()

@Suppress("UNCHECKED_CAST")
public val <T : Table<*>> T.columns: Set<AbstractColumn<T, *>>
    get() = this::class.declaredMemberProperties.find { it.name == "kotysaColumns" }!!.getter.call(this)!! as Set<AbstractColumn<T, *>>

@Suppress("UNCHECKED_CAST")
public val <T : Table<*>> T.foreignKeys: Set<ForeignKey<T, *>>
    get() = this::class.declaredMemberProperties.find { it.name == "kotysaForeignKeys" }!!.getter.call(this)!! as Set<ForeignKey<T, *>>

@Suppress("UNCHECKED_CAST")
public val <T : Table<*>> ForeignKey<T, *>.referencedColumns: Map<AbstractDbColumn<T, *>, AbstractDbColumn<*, *>>
    get() = this::class.declaredMemberProperties.find { it.name == "references" }!!.getter.call(this)!! as Map<AbstractDbColumn<T, *>, AbstractDbColumn<*, *>>

@Suppress("UNCHECKED_CAST")
public fun <T : Table<*>> ForeignKey<T, *>.referencedTable(tables: List<T>): T = referencedColumns.entries.first().let { (_, referencedColumn) ->
    tables.single { table -> table.columns.any { it === referencedColumn } }
}

private fun <T : Table<*>> getKotysaTables(
    kClass: KClass<T>,
    config: TableConfig,
): List<T> = getTables<T>(
    kClass,
    config,
) { it.foreignKeys.map { foreignKey -> foreignKey.referencedTable(this) } }

public fun getKotysaH2Tables(config: TableConfig): List<IH2Table<*>> =
    getKotysaTables(IH2Table::class, config) + getKotysaTables(
        GenericTable::class,
        config,
    )

public fun getKotysaMariadbTables(config: TableConfig): List<MariadbTable<*>> =
    getKotysaTables(MariadbTable::class, config)

public fun getKotysaMysqlTables(config: TableConfig): List<MysqlTable<*>> =
    getKotysaTables(MysqlTable::class, config)

public fun getKotysaMssqlTables(config: TableConfig): List<IMssqlTable<*>> =
    getKotysaTables(MssqlTable::class, config) + getKotysaTables(
        GenericTable::class,
        config,
    )

public fun getKotysaPostgresqlTables(config: TableConfig): List<IPostgresqlTable<*>> =
    getKotysaTables(PostgresqlTable::class, config) + getKotysaTables(
        GenericTable::class,
        config,
    )

public fun getKotysaOracleTables(config: TableConfig): List<OracleTable<*>> =
    getKotysaTables(OracleTable::class, config)

public fun getKotysaTables(driver: String, configs: List<TableConfig>): List<Table<*>> =
    when (driver) {
        "h2" -> configs.flatMap(::getKotysaH2Tables)

        "postgresql" -> configs.flatMap(::getKotysaPostgresqlTables)

        "mysql" -> configs.flatMap(::getKotysaMysqlTables)

        "mssql" -> configs.flatMap(::getKotysaMssqlTables)

        "mariadb" -> configs.flatMap(::getKotysaMariadbTables)

        "oracle" -> configs.flatMap(::getKotysaOracleTables)

        else -> throw IllegalArgumentException("Unknown database driver \"$driver\"")
    }

public fun getKotysaTable(tableName: String, driver: String, configs: List<TableConfig>): Table<*>? =
    getKotysaTables(driver, configs).find { it.name == tableName }
