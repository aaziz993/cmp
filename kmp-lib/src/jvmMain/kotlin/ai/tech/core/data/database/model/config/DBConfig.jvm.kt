package ai.tech.core.data.database.model.config

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import javax.sql.DataSource

import io.r2dbc.pool.PoolingConnectionFactoryProvider
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions
import kotlin.time.toJavaDuration

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
            },
        )

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
