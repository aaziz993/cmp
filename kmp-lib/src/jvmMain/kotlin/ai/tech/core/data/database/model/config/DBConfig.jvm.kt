package ai.tech.core.data.database.model.config

import ai.tech.core.data.database.exposed.getExposedTables
import ai.tech.core.data.database.kotysa.getKotysaH2Tables
import ai.tech.core.data.database.kotysa.getKotysaMariadbTables
import ai.tech.core.data.database.kotysa.getKotysaMssqlTables
import ai.tech.core.data.database.kotysa.getKotysaMysqlTables
import ai.tech.core.data.database.kotysa.getKotysaOracleTables
import ai.tech.core.data.database.kotysa.getKotysaPostgresqlTables
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.r2dbc.pool.PoolingConnectionFactoryProvider
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import javax.sql.DataSource
import kotlin.time.toJavaDuration
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.DatabaseConfig
import org.jetbrains.exposed.sql.ExperimentalKeywordApi
import org.jetbrains.exposed.sql.Schema
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.exists
import org.jetbrains.exposed.sql.transactions.transaction
import org.ufoss.kotysa.R2dbcSqlClient
import org.ufoss.kotysa.Table
import org.ufoss.kotysa.r2dbc.coSqlClient
import org.ufoss.kotysa.tables

public val DBConfig.hikariDataSource: DataSource
    get() =
        HikariDataSource(
            HikariConfig().apply {
                jdbcUrl = this@hikariDataSource.jdbcUrl
                username = this@hikariDataSource.user
                password = this@hikariDataSource.password
                this@hikariDataSource.initPoolSize?.let { minimumIdle = it }
                this@hikariDataSource.maxPoolSize?.let { maximumPoolSize = it }
                this@hikariDataSource.connectTimeout?.let { connectionTimeout = it.inWholeMilliseconds }
                this@hikariDataSource.lockWaitTimeout?.let { idleTimeout = it.inWholeMilliseconds }
                this@hikariDataSource.statementTimeout?.let { maxLifetime = it.inWholeMilliseconds }
                this@hikariDataSource.validationTimeout?.let { validationTimeout = it.inWholeMilliseconds }
                this@hikariDataSource.initializationFailTimeout?.let { initializationFailTimeout = it.inWholeMilliseconds }
                this@hikariDataSource.keepaliveTime?.let { keepaliveTime = it.inWholeMilliseconds }
                this@hikariDataSource.isAutoCommit?.let { isAutoCommit = it }
                // As of exposed version 0.46.0, if these options are set here, they do not need to be duplicated in DatabaseConfig
                isReadOnly = this@hikariDataSource.isReadOnly
                transactionIsolation = this@hikariDataSource.transactionIsolation
            },
        )

@OptIn(ExperimentalKeywordApi::class)
public val DBConfig.exposedDatabase: Database
    get() = Database.connect(
        hikariDataSource,
        databaseConfig = DatabaseConfig {
            this@exposedDatabase.defaultFetchSize?.let { defaultFetchSize = it }
            this@exposedDatabase.defaultIsolationLevel?.let { defaultIsolationLevel = it }
            this@exposedDatabase.defaultMaxAttempts?.let { defaultMaxAttempts = it }
            this@exposedDatabase.defaultMinRetryDelay?.let { defaultMinRetryDelay = it }
            this@exposedDatabase.defaultMaxRetryDelay?.let { defaultMaxRetryDelay = it }
            this@exposedDatabase.warnLongQueriesDuration?.let { warnLongQueriesDuration = it }
            this@exposedDatabase.maxEntitiesToStoreInCachePerEntity?.let { maxEntitiesToStoreInCachePerEntity = it }
            this@exposedDatabase.keepLoadedReferencesOutOfTransaction?.let { keepLoadedReferencesOutOfTransaction = it }
            this@exposedDatabase.logTooMuchResultSetsThreshold?.let { logTooMuchResultSetsThreshold = it }
            this@exposedDatabase.preserveKeywordCasing?.let { preserveKeywordCasing = it }
        },
    ).also { database ->
        transaction(database) {
            addLogger(StdOutSqlLogger)

            this@exposedDatabase.defaultSchema?.let {
                val schema = Schema(it.name)

                if (schema.exists()) {
                    if (it.create != Creation.IF_NOT_EXISTS) {
                        SchemaUtils.dropSchema(schema)

                        SchemaUtils.createSchema(schema, inBatch = it.createInBatch)
                    }
                }
                else {
                    SchemaUtils.createSchema(schema)
                }

                SchemaUtils.setSchema(schema)
            }



            this@exposedDatabase.table.filterNot { it.create == Creation.SKIP }.forEach {

                val (existTables, newTables) = getExposedTables(it).partition { it.exists() }
                    .let { it.first.toTypedArray() to it.second.toTypedArray() }

                if (it.create == Creation.OVERRIDE) {
                    SchemaUtils.drop(*existTables, inBatch = it.createInBatch)
                    SchemaUtils.create(*existTables, inBatch = it.createInBatch)
                }

                SchemaUtils.create(*newTables, inBatch = it.createInBatch)
            }
        }
    }

public val DBConfig.r2dbcConnectionFactory: ConnectionFactory
    get() =
        ConnectionFactories.get(
            ConnectionFactoryOptions.builder()
                .option(ConnectionFactoryOptions.DRIVER, driver)
                .option(ConnectionFactoryOptions.USER, user)
                .option(ConnectionFactoryOptions.PASSWORD, password)
                .option(ConnectionFactoryOptions.DATABASE, database)
                .option(ConnectionFactoryOptions.PROTOCOL, protocol)
                .option(ConnectionFactoryOptions.HOST, host)
                .option(ConnectionFactoryOptions.PORT, port)
                .option(ConnectionFactoryOptions.SSL, ssl)
                .apply {
                    if (initPoolSize != null) {
                        option(PoolingConnectionFactoryProvider.INITIAL_SIZE, initPoolSize) // Optional: Initial pool size
                    }

                    if (maxPoolSize != null) {
                        option(PoolingConnectionFactoryProvider.MAX_SIZE, maxPoolSize) // Max pool size
                    }

                    if (connectTimeout != null) {
                        option(ConnectionFactoryOptions.CONNECT_TIMEOUT, connectTimeout.toJavaDuration())
                    }

                    if (lockWaitTimeout != null) {
                        option(ConnectionFactoryOptions.LOCK_WAIT_TIMEOUT, lockWaitTimeout.toJavaDuration())
                    }

                    if (statementTimeout != null) {
                        option(ConnectionFactoryOptions.STATEMENT_TIMEOUT, statementTimeout.toJavaDuration())
                    }
                }
                .build(),
        )

public suspend fun DBConfig.getKotysaR2dbcClient(): R2dbcSqlClient {
    val r2dbcConnectionFactory = r2dbcConnectionFactory

    val createTables: List<Pair<List<Table<*>>, Creation>>

    val client: R2dbcSqlClient

    when (driver) {
        "h2" -> {
            createTables = table.map { getKotysaH2Tables(it) to it.create }
            client = r2dbcConnectionFactory.coSqlClient(tables().h2(* createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "postgresql" -> {
            createTables = table.map { getKotysaPostgresqlTables(it) to it.create }
            client = r2dbcConnectionFactory.coSqlClient(tables().postgresql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mysql" -> {
            createTables = table.map { getKotysaMysqlTables(it) to it.create }
            client = r2dbcConnectionFactory.coSqlClient(tables().mysql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mssql" -> {
            createTables = table.map { getKotysaMssqlTables(it) to it.create }
            client = r2dbcConnectionFactory.coSqlClient(tables().mssql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mariadb" -> {
            createTables = table.map { getKotysaMariadbTables(it) to it.create }
            client = r2dbcConnectionFactory.coSqlClient(tables().mariadb(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "oracle" -> {
            createTables = table.map { getKotysaOracleTables(it) to it.create }
            client = r2dbcConnectionFactory.coSqlClient(tables().oracle(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        else -> throw UnsupportedOperationException("Unknown database type \"$driver\"")
    }

    createTables.forEach { (tables, create) ->
        when (create) {
            Creation.IF_NOT_EXISTS -> tables.forEach { client createTableIfNotExists it }

            Creation.OVERRIDE -> tables.forEach() {
                client deleteAllFrom it
                client createTable it
            }

            else -> Unit
        }
    }

    return client
}
