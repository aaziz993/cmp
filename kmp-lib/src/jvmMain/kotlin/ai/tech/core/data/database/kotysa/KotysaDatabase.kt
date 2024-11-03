package ai.tech.core.data.database.kotysa

import ai.tech.core.data.database.model.config.CreateDatabaseTableConfig
import ai.tech.core.data.database.model.config.DatabaseProviderConfig
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import java.lang.UnsupportedOperationException
import kotlin.reflect.KClass
import kotlinx.coroutines.runBlocking
import org.reflections.Reflections
import org.reflections.scanners.Scanners.SubTypes
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

public fun createKotysaR2dbcClient(config: DatabaseProviderConfig): R2dbcSqlClient =
    when (config.connection.driver) {
        "h2" -> createClient(config, { getH2Tables(it) }) { connectionFactory, tables ->
            connectionFactory.coSqlClient(tables().h2(*tables))
        }

        "postgresql" -> createClient(config, { getPostgresqlTables(it) }) { connectionFactory, tables ->
            connectionFactory.coSqlClient(tables().postgresql(*tables))
        }

        "mysql" -> createClient(config, { getMysqlTables(it) }) { connectionFactory, tables ->
            connectionFactory.coSqlClient(tables().mysql(*tables))
        }

        "mssql" -> createClient(config, { getMssqlTables(it) }) { connectionFactory, tables ->
            connectionFactory.coSqlClient(tables().mssql(*tables))
        }

        "mariadb" -> createClient(config, { getMariadbTables(it) }) { connectionFactory, tables ->
            connectionFactory.coSqlClient(tables().mariadb(*tables))
        }

        "oracle" -> createClient(config, { getOracleTables(it) }) { connectionFactory, tables ->
            connectionFactory.coSqlClient(tables().oracle(*tables))
        }

        else -> throw UnsupportedOperationException("Unknown database type \"${config.connection.driver}\"")
    }

private inline fun <reified T : Table<*>> createClient(
    config: DatabaseProviderConfig,
    tables: (CreateDatabaseTableConfig) -> List<T>,
    crossinline client: (ConnectionFactory, Array<T>) -> R2dbcSqlClient
): R2dbcSqlClient =
    config.createTables.map {
        tables(it) to it.ifNotExists
    }.let { createTables ->
        client(config.getConnectionFactory(), createTables.flatMap { it.first }.toTypedArray()).also {
            createDatabaseTables(it, createTables)
        }
    }

private fun DatabaseProviderConfig.getConnectionFactory(): ConnectionFactory = with(connection) {
    ConnectionFactories.get(
        ConnectionFactoryOptions.builder()
            .option(
                ConnectionFactoryOptions.DRIVER,
                driver,
            )
            .option(
                ConnectionFactoryOptions.USER,
                user,
            )
            .option(
                ConnectionFactoryOptions.PASSWORD,
                password,
            )
            .option(
                ConnectionFactoryOptions.DATABASE,
                this.database,
            ).also { connectionBuilderOptions ->
                protocol?.let {
                    connectionBuilderOptions
                        .option(
                            ConnectionFactoryOptions.PROTOCOL,
                            it,
                        )
                }
                host.let {
                    connectionBuilderOptions
                        .option(
                            ConnectionFactoryOptions.HOST,
                            it,
                        )
                }
                port.let {
                    connectionBuilderOptions
                        .option(
                            ConnectionFactoryOptions.PORT,
                            it,
                        )
                }
            }.build(),
    )
}

private fun createDatabaseTables(
    client: R2dbcSqlClient,
    tables: List<Pair<List<Table<*>>, Boolean>>,
) =
    runBlocking {
        tables.forEach {
            if (it.second) {
                for (table in it.first) {
                    client createTableIfNotExists table
                }
            }
            else {
                for (table in it.first) {
                    client deleteAllFrom table
                    client createTable table
                }
            }
        }
    }

private fun <T : Table<*>> getTables(
    config: CreateDatabaseTableConfig,
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

private fun getH2Tables(config: CreateDatabaseTableConfig): List<IH2Table<*>> =
    getTables(config, IH2Table::class) + getTables(
        config,
        GenericTable::class,
    )

private fun getMariadbTables(config: CreateDatabaseTableConfig): List<MariadbTable<*>> =
    getTables(config, MariadbTable::class)

private fun getMysqlTables(config: CreateDatabaseTableConfig): List<MysqlTable<*>> =
    getTables(config, MysqlTable::class)

private fun getMssqlTables(config: CreateDatabaseTableConfig): List<IMssqlTable<*>> =
    getTables(config, MssqlTable::class) + getTables(
        config,
        GenericTable::class,
    )

private fun getPostgresqlTables(config: CreateDatabaseTableConfig): List<IPostgresqlTable<*>> =
    getTables(config, PostgresqlTable::class) + getTables(
        config,
        GenericTable::class,
    )

private fun getOracleTables(config: CreateDatabaseTableConfig): List<OracleTable<*>> =
    getTables(config, OracleTable::class)
