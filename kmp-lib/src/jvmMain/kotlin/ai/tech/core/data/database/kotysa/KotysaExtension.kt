package ai.tech.core.data.database.kotysa

import ai.tech.core.data.database.getTables
import ai.tech.core.data.database.model.config.DBConfig
import ai.tech.core.data.database.model.config.TableConfig
import java.lang.IllegalArgumentException
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
private val <T:Table<*>> T.columns: Set<AbstractColumn<T, *>>
    get() = this::class.declaredMemberProperties.find { it.name == "kotysaColumns" }!!.getter.call(this)!! as Set<AbstractColumn<T, *>>

@Suppress("UNCHECKED_CAST")
private val <T: Table<*>> T.foreignKeys: Set<ForeignKey<T, *>>
    get() = this::class.declaredMemberProperties.find { it.name == "kotysaForeignKeys" }!!.getter.call(this)!! as Set<ForeignKey<T, *>>

@Suppress("UNCHECKED_CAST")
private val <T : Table<*>> ForeignKey<T, *>.referencedColumns: Map<AbstractDbColumn<T, *>, AbstractDbColumn<*, *>>
    get() = this::class.declaredMemberProperties.find { it.name == "references" }!!.getter.call(this)!! as Map<AbstractDbColumn<T, *>, AbstractDbColumn<*, *>>

@Suppress("UNCHECKED_CAST")
private fun <T : Table<*>> ForeignKey<T, *>.referencedTables(tables: List<T>): List<T> = referencedColumns.keys.map { referencedColumn ->
    tables.single { table -> table.columns.contains(referencedColumn) }
}

private fun <T : Table<*>> getKotysaTables(
    kClass: KClass<T>,
    config: TableConfig,
): List<T> = getTables<T>(
    kClass,
    config,
) { it.foreignKeys.flatMap { foreignKey -> foreignKey.referencedTables<T>(this) } }

private fun getKotysaH2Tables(config: TableConfig): List<IH2Table<*>> =
    getKotysaTables(IH2Table::class, config) + getKotysaTables(
        GenericTable::class,
        config,
    )

private fun getKotysaMariadbTables(config: TableConfig): List<MariadbTable<*>> =
    getKotysaTables(MariadbTable::class, config)

private fun getKotysaMysqlTables(config: TableConfig): List<MysqlTable<*>> =
    getKotysaTables(MysqlTable::class, config)

private fun getKotysaMssqlTables(config: TableConfig): List<IMssqlTable<*>> =
    getKotysaTables(MssqlTable::class, config) + getKotysaTables(
        GenericTable::class,
        config,
    )

private fun getKotysaPostgresqlTables(config: TableConfig): List<IPostgresqlTable<*>> =
    getKotysaTables(PostgresqlTable::class, config) + getKotysaTables(
        GenericTable::class,
        config,
    )

private fun getKotysaOracleTables(config: TableConfig): List<OracleTable<*>> =
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
