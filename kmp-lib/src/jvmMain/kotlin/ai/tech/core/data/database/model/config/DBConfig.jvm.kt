package ai.tech.core.data.database.model.config

import ai.tech.core.data.transaction.model.hikariTransactionIsolation
import ai.tech.core.data.database.exposed.getExposedTables
import ai.tech.core.data.database.kotysa.getKotysaH2Tables
import ai.tech.core.data.database.kotysa.getKotysaMariadbTables
import ai.tech.core.data.database.kotysa.getKotysaMssqlTables
import ai.tech.core.data.database.kotysa.getKotysaMysqlTables
import ai.tech.core.data.database.kotysa.getKotysaOracleTables
import ai.tech.core.data.database.kotysa.getKotysaPostgresqlTables
import ai.tech.core.data.database.r2dbc.AUTO_COMMIT
import ai.tech.core.data.database.r2dbc.R2dbcConnectionFactory
import ai.tech.core.data.database.r2dbc.USE_NESTED_TRANSACTIONS
import ai.tech.core.misc.model.config.EnabledConfig
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
                this@hikariDataSource.isReadOnly?.let { isReadOnly = it }
                this@hikariDataSource.transactionIsolation?.let { transactionIsolation = it.hikariTransactionIsolation }
            },
        )

@OptIn(ExperimentalKeywordApi::class)
public val DBConfig.exposedDatabase: Database
    get() = Database.connect(
        hikariDataSource,
        databaseConfig = DatabaseConfig {
            this@exposedDatabase.useNestedTransactions?.let { useNestedTransactions = it }
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
                    if (it.create == Creation.OVERRIDE) {
                        SchemaUtils.dropSchema(schema)

                        SchemaUtils.createSchema(schema, inBatch = it.createInBatch)
                    }
                }
                else {
                    if (it.create == Creation.IF_NOT_EXISTS) {
                        SchemaUtils.createSchema(schema)
                    }
                }

                SchemaUtils.setSchema(schema)
            }



            this@exposedDatabase.table.filter(EnabledConfig::enabled).filterNot { it.create == Creation.SKIP }.forEach { config ->

                val (existTables, newTables) = getExposedTables(
                    config.tables,
                    config.scanPackage,
                    config.excludePatterns,
                ).partition { it.exists() }
                    .let { it.first.toTypedArray() to it.second.toTypedArray() }

                if (config.create == Creation.OVERRIDE) {
                    SchemaUtils.drop(*existTables, inBatch = config.createInBatch)
                    SchemaUtils.create(*existTables, inBatch = config.createInBatch)
                }

                if (config.create == Creation.IF_NOT_EXISTS) {
                    SchemaUtils.create(*newTables, inBatch = config.createInBatch)
                }
            }
        }
    }

public val DBConfig.jdbc: Database
    get() = exposedDatabase

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
        ).let { connectionFactory ->
            R2dbcConnectionFactory(
                connectionFactory,
                ConnectionFactoryOptions.builder()
                    .option(AUTO_COMMIT, isAutoCommit == true)
                    .option(USE_NESTED_TRANSACTIONS, useNestedTransactions == true)
                    .build(),
            )
        }

public suspend fun DBConfig.kotysaR2dbcClient(): R2dbcSqlClient {
    val r2dbcConnectionFactory = r2dbcConnectionFactory

    val createTables: List<Pair<List<Table<*>>, Creation>>

    val client: R2dbcSqlClient

    val tableConfig = table.filter(EnabledConfig::enabled)

    when (driver) {
        "h2" -> {
            createTables = tableConfig.map { config ->
                getKotysaH2Tables(
                    config.tables,
                    config.scanPackage,
                    config.excludePatterns,
                ) to config.create
            }
            client = r2dbcConnectionFactory.coSqlClient(tables().h2(* createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "postgresql" -> {
            createTables = tableConfig.map { config ->
                getKotysaPostgresqlTables(
                    config.tables,
                    config.scanPackage,
                    config.excludePatterns,
                ) to config.create
            }
            client = r2dbcConnectionFactory.coSqlClient(tables().postgresql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mysql" -> {
            createTables = tableConfig.map { config ->
                getKotysaMysqlTables(
                    config.tables,
                    config.scanPackage,
                    config.excludePatterns,
                ) to config.create
            }
            client = r2dbcConnectionFactory.coSqlClient(tables().mysql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mssql" -> {
            createTables = tableConfig.map { config ->
                getKotysaMssqlTables(
                    config.tables,
                    config.scanPackage,
                    config.excludePatterns,
                ) to config.create
            }
            client = r2dbcConnectionFactory.coSqlClient(tables().mssql(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "mariadb" -> {
            createTables = tableConfig.map { config ->
                getKotysaMariadbTables(
                    config.tables,
                    config.scanPackage,
                    config.excludePatterns,
                ) to config.create
            }
            client = r2dbcConnectionFactory.coSqlClient(tables().mariadb(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        "oracle" -> {
            createTables = tableConfig.map { config ->
                getKotysaOracleTables(
                    config.tables,
                    config.scanPackage,
                    config.excludePatterns,
                ) to config.create
            }
            client = r2dbcConnectionFactory.coSqlClient(tables().oracle(*createTables.flatMap { (tables, _) -> tables }.toTypedArray()))
        }

        else -> throw UnsupportedOperationException("Unknown database type \"$driver\"")
    }

    createTables.filterNot { (_, create) -> create == Creation.SKIP }.forEach { (tables, create) ->
        if (create == Creation.IF_NOT_EXISTS) {
            tables.forEach { table -> client createTableIfNotExists table }
        }
        else {
            tables.forEach { table ->
                client deleteAllFrom table
                client createTable table
            }
        }
    }

    return client
}

public suspend fun DBConfig.r2dbc(): R2dbcSqlClient = kotysaR2dbcClient()
