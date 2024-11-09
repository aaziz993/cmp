package ai.tech.core.data.database.kotysa

import ai.tech.core.data.database.model.config.CreateTableConfig
import ai.tech.core.data.database.model.config.DatabaseProviderConfig
import ai.tech.core.data.database.r2dbc.*
import java.lang.UnsupportedOperationException
import kotlin.reflect.KClass
import kotlin.reflect.full.declaredMemberProperties
import org.reflections.Reflections
import org.reflections.scanners.Scanners.SubTypes
import org.ufoss.kotysa.AbstractTable
import org.ufoss.kotysa.GenericTable
import org.ufoss.kotysa.R2dbcSqlClient
import org.ufoss.kotysa.Table
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

public suspend fun createClient(config: DatabaseProviderConfig): R2dbcSqlClient {
    val connectionFactory = createConnectionFactory(config.connection)

    val createTables: List<Pair<List<Table<*>>, Boolean>>

    val client: R2dbcSqlClient

    when (config.connection.driver) {
        "h2" -> {
            createTables = config.createTables.map { getH2Tables(it) to it.ifNotExists }
            client = connectionFactory.coSqlClient(tables().h2(* createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "postgresql" -> {
            createTables = config.createTables.map { getPostgresqlTables(it) to it.ifNotExists }
            client = connectionFactory.coSqlClient(tables().postgresql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mysql" -> {
            createTables = config.createTables.map { getMysqlTables(it) to it.ifNotExists }
            client = connectionFactory.coSqlClient(tables().mysql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mssql" -> {
            createTables = config.createTables.map { getMssqlTables(it) to it.ifNotExists }
            client = connectionFactory.coSqlClient(tables().mssql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mariadb" -> {
            createTables = config.createTables.map { getMariadbTables(it) to it.ifNotExists }
            client = connectionFactory.coSqlClient(tables().mariadb(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "oracle" -> {
            createTables = config.createTables.map { getOracleTables(it) to it.ifNotExists }
            client = connectionFactory.coSqlClient(tables().oracle(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
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
    }

public fun getH2Tables(config: CreateTableConfig): List<IH2Table<*>> =
    getTables(config, IH2Table::class) + getTables(
        config,
        GenericTable::class,
    )

public fun getMariadbTables(config: CreateTableConfig): List<MariadbTable<*>> =
    getTables(config, MariadbTable::class)

public fun getMysqlTables(config: CreateTableConfig): List<MysqlTable<*>> =
    getTables(config, MysqlTable::class)

public fun getMssqlTables(config: CreateTableConfig): List<IMssqlTable<*>> =
    getTables(config, MssqlTable::class) + getTables(
        config,
        GenericTable::class,
    )

public fun getPostgresqlTables(config: CreateTableConfig): List<IPostgresqlTable<*>> =
    getTables(config, PostgresqlTable::class) + getTables(
        config,
        GenericTable::class,
    )

public fun getOracleTables(config: CreateTableConfig): List<OracleTable<*>> =
    getTables(config, OracleTable::class)

public val AbstractTable<*>.name: String
    get() = this::class.declaredMemberProperties.find { it.name == "tableName" }!!.getter.call(this)!!.toString()


