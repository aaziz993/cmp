package ai.tech.core.data.database.kotysa

import ai.tech.core.data.database.model.config.CreateTableConfig
import ai.tech.core.data.database.model.config.DatabaseProviderConfig
import ai.tech.core.data.database.r2dbc.*
import java.lang.UnsupportedOperationException
import kotlin.collections.mutableSetOf
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
public val <T : Any, U : Any> ForeignKey<T, U>.referencesMap: Map<AbstractDbColumn<T, *>, AbstractDbColumn<U, *>>
    get() = this::class.declaredMemberProperties.find { it.name == "references" }!!.getter.call(this)!! as Map<AbstractDbColumn<T, *>, AbstractDbColumn<U, *>>

public suspend fun createKotysaR2dbcSqlClient(config: DatabaseProviderConfig): R2dbcSqlClient {
    val r2dbcConnectionFactory = createR2dbcConnectionFactory(config.connection)

    val createTables: List<Pair<List<Table<*>>, Boolean>>

    val client: R2dbcSqlClient

    when (config.connection.driver) {
        "h2" -> {
            createTables = config.createTables.map { getKotysaH2Tables(it) to it.ifNotExists }
            client = r2dbcConnectionFactory.coSqlClient(tables().h2(* createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "postgresql" -> {
            createTables = config.createTables.map { getKotysaPostgresqlTables(it) to it.ifNotExists }
            client = r2dbcConnectionFactory.coSqlClient(tables().postgresql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mysql" -> {
            createTables = config.createTables.map { getKotysaMysqlTables(it) to it.ifNotExists }
            client = r2dbcConnectionFactory.coSqlClient(tables().mysql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mssql" -> {
            createTables = config.createTables.map { getKotysaMssqlTables(it) to it.ifNotExists }
            client = r2dbcConnectionFactory.coSqlClient(tables().mssql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mariadb" -> {
            createTables = config.createTables.map { getKotysaMariadbTables(it) to it.ifNotExists }
            client = r2dbcConnectionFactory.coSqlClient(tables().mariadb(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "oracle" -> {
            createTables = config.createTables.map { getKotysaOracleTables(it) to it.ifNotExists }
            client = r2dbcConnectionFactory.coSqlClient(tables().oracle(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        else -> throw UnsupportedOperationException("Unknown database type \"${config.connection.driver}\"")
    }

    createTables.forEach { (tables, ifNotExists) ->
        if (ifNotExists) {
            tables.forEach { client createTableIfNotExists it }
        }
        else {
            tables.forEach() {
                client deleteAllFrom it
                client createTable it
            }
        }
    }

    return client
}

@Suppress("UNCHECKED_CAST")
private fun <T : Table<*>> getTables(
    config: CreateTableConfig,
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

private fun <T : Table<*>> List<T>.sortedByDependencies(): List<T> {
    val dependencyMap = associateWith { mutableSetOf<Table<*>>() }

    forEach { table ->
        table.foreignKeys.forEach { foreignKey ->
            val referencedTables = foreignKey.referencesMap.keys.map { referencedColumn ->
                single { table -> table.columns.contains(referencedColumn) } as Table<*>
            }

            dependencyMap[table]?.addAll(referencedTables)
        }
    }

    val sortedTables = mutableListOf<T>()

    val mainTables = dependencyMap.filterValues { it.isEmpty() }.keys.toMutableList()

    while (mainTables.isNotEmpty()) {
        val table = mainTables.removeAt(0)

        sortedTables.add(table)

        dependencyMap.forEach { (dependantTable, dependencies) ->
            if (dependencies.remove(table) && dependencies.isEmpty()) {
                mainTables.add(dependantTable)
            }
        }
    }

    if (sortedTables.size != size) {
        throw IllegalStateException("Circular dependency detected among tables!")
    }

    return sortedTables
}

public fun getKotysaH2Tables(config: CreateTableConfig): List<IH2Table<*>> =
    getTables(config, IH2Table::class) + getTables(
        config,
        GenericTable::class,
    )

public fun getKotysaMariadbTables(config: CreateTableConfig): List<MariadbTable<*>> =
    getTables(config, MariadbTable::class)

public fun getKotysaMysqlTables(config: CreateTableConfig): List<MysqlTable<*>> =
    getTables(config, MysqlTable::class)

public fun getKotysaMssqlTables(config: CreateTableConfig): List<IMssqlTable<*>> =
    getTables(config, MssqlTable::class) + getTables(
        config,
        GenericTable::class,
    )

public fun getKotysaPostgresqlTables(config: CreateTableConfig): List<IPostgresqlTable<*>> =
    getTables(config, PostgresqlTable::class) + getTables(
        config,
        GenericTable::class,
    )

public fun getKotysaOracleTables(config: CreateTableConfig): List<OracleTable<*>> =
    getTables(config, OracleTable::class)


