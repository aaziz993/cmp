package ai.tech.core.misc.kotysa

import ai.tech.core.data.database.model.config.TableConfig
import ai.tech.core.data.database.model.config.DatabaseProviderConfig
import ai.tech.core.data.database.model.config.TableCreation
import ai.tech.core.misc.r2dbc.createR2dbcConnectionFactory
import ai.tech.core.misc.type.multiple.whileIndexed
import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException
import kotlin.collections.contains
import kotlin.collections.single
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import org.reflections.Reflections
import org.reflections.scanners.Scanners.SubTypes
import org.ufoss.kotysa.ForeignKey
import org.ufoss.kotysa.GenericTable
import org.ufoss.kotysa.R2dbcSqlClient
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
import org.ufoss.kotysa.r2dbc.coSqlClient
import org.ufoss.kotysa.tables

public val Table<*>.name: String
    get() = this::class.declaredMemberProperties.find { it.name == "tableName" }!!.getter.call(this)!!.toString()

@Suppress("UNCHECKED_CAST")
public val <T : Any> Table<T>.columns: Set<AbstractColumn<T, *>>
    get() = this::class.declaredMemberProperties.find { it.name == "kotysaColumns" }!!.getter.call(this)!! as Set<AbstractColumn<T, *>>

@Suppress("UNCHECKED_CAST")
public val <T : Any> Table<T>.foreignKeys: Set<ForeignKey<T, *>>
    get() = this::class.declaredMemberProperties.find { it.name == "kotysaForeignKeys" }!!.getter.call(this)!! as Set<ForeignKey<T, *>>

@Suppress("UNCHECKED_CAST")
public val <T : Any, U : Any> ForeignKey<T, U>.referencedColumns: Map<AbstractDbColumn<T, *>, AbstractDbColumn<U, *>>
    get() = this::class.declaredMemberProperties.find { it.name == "references" }!!.getter.call(this)!! as Map<AbstractDbColumn<T, *>, AbstractDbColumn<U, *>>

@Suppress("UNCHECKED_CAST")
public fun <T : Any, U : Any> ForeignKey<T, U>.referencedTables(tables: List<Table<*>>): List<Table<*>> = referencedColumns.keys.map { referencedColumn ->
    tables.single { table -> table.columns.contains(referencedColumn) }
}

public suspend fun createKotysaR2dbcSqlClient(config: DatabaseProviderConfig): R2dbcSqlClient {
    val r2dbcConnectionFactory = createR2dbcConnectionFactory(config.connection)

    val createTables: List<Pair<List<Table<*>>, TableCreation>>

    val client: R2dbcSqlClient

    when (config.connection.driver) {
        "h2" -> {
            createTables = config.table.map { getKotysaH2Tables(it) to it.create }
            client = r2dbcConnectionFactory.coSqlClient(tables().h2(* createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "postgresql" -> {
            createTables = config.table.map { getKotysaPostgresqlTables(it) to it.create }
            client = r2dbcConnectionFactory.coSqlClient(tables().postgresql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mysql" -> {
            createTables = config.table.map { getKotysaMysqlTables(it) to it.create }
            client = r2dbcConnectionFactory.coSqlClient(tables().mysql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mssql" -> {
            createTables = config.table.map { getKotysaMssqlTables(it) to it.create }
            client = r2dbcConnectionFactory.coSqlClient(tables().mssql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mariadb" -> {
            createTables = config.table.map { getKotysaMariadbTables(it) to it.create }
            client = r2dbcConnectionFactory.coSqlClient(tables().mariadb(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "oracle" -> {
            createTables = config.table.map { getKotysaOracleTables(it) to it.create }
            client = r2dbcConnectionFactory.coSqlClient(tables().oracle(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        else -> throw UnsupportedOperationException("Unknown database type \"${config.connection.driver}\"")
    }

    createTables.forEach { (tables, create) ->
        when (create) {
            TableCreation.IF_NOT_EXISTS -> tables.forEach { client createTableIfNotExists it }

            TableCreation.OVERRIDE -> tables.forEach() {
                client deleteAllFrom it
                client createTable it
            }

            else -> Unit
        }
    }

    return client
}

@Suppress("UNCHECKED_CAST")
private fun <T : Table<*>> getTables(
    config: TableConfig,
    type: KClass<T>
): List<T> =
    config.packages.flatMap {
        Reflections(it).get(SubTypes.of(type.java).asClass<T>()).map {
            it
        }.let {
            if (config.inclusive) {
                it.filter { it.simpleName in config.names }
            }
            else {
                it.filter { it.simpleName !in config.names }
            }
        }.map {
            it.kotlin.objectInstance as T
        }
    }.sortedByDependencies()

public fun <T : Table<*>> List<T>.sortedByDependencies(): List<T> {

    val (tables, dependentTables) = partition { it.foreignKeys.isEmpty() }.let {
        it.first.toMutableList() to it.second.associateWith {
            it.foreignKeys.flatMap { foreignKey -> foreignKey.referencedTables(this) }.toMutableSet()
        }
    }

    tables.whileIndexed { _, table ->
        dependentTables.forEach { (dependantTable, dependencies) ->
            if (dependencies.remove(table) && dependencies.isEmpty()) {
                tables.add(dependantTable)
            }
        }
    }

    if (tables.size != size) {
        throw IllegalStateException("Circular dependency detected among tables!")
    }

    return tables
}

public fun getKotysaH2Tables(config: TableConfig): List<IH2Table<*>> =
    getTables(config, IH2Table::class) + getTables(
        config,
        GenericTable::class,
    )

public fun getKotysaMariadbTables(config: TableConfig): List<MariadbTable<*>> =
    getTables(config, MariadbTable::class)

public fun getKotysaMysqlTables(config: TableConfig): List<MysqlTable<*>> =
    getTables(config, MysqlTable::class)

public fun getKotysaMssqlTables(config: TableConfig): List<IMssqlTable<*>> =
    getTables(config, MssqlTable::class) + getTables(
        config,
        GenericTable::class,
    )

public fun getKotysaPostgresqlTables(config: TableConfig): List<IPostgresqlTable<*>> =
    getTables(config, PostgresqlTable::class) + getTables(
        config,
        GenericTable::class,
    )

public fun getKotysaOracleTables(config: TableConfig): List<OracleTable<*>> =
    getTables(config, OracleTable::class)

public fun getKotysaTables(config: DatabaseProviderConfig): List<Table<*>> =
    when (config.connection.driver) {
        "h2" -> config.table.flatMap(::getKotysaH2Tables)

        "postgresql" -> config.table.flatMap(::getKotysaPostgresqlTables)

        "mysql" -> config.table.flatMap(::getKotysaMysqlTables)

        "mssql" -> config.table.flatMap(::getKotysaMssqlTables)

        "mariadb" -> config.table.flatMap(::getKotysaMariadbTables)

        "oracle" -> config.table.flatMap(::getKotysaOracleTables)

        else -> throw UnsupportedOperationException("Unknown database type \"${config.connection.driver}\"")
    }
