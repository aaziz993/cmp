package ai.tech.core.misc.r2dbc

import ai.tech.core.data.database.model.config.DatabaseConnectionConfig
import io.r2dbc.spi.ConnectionFactories
import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions

public fun createR2dbcConnectionFactory(config: DatabaseConnectionConfig): ConnectionFactory = with(config) {
    ConnectionFactories.get(
        ConnectionFactoryOptions.builder()
            .option(ConnectionFactoryOptions.DRIVER, driver)
            .option(ConnectionFactoryOptions.USER, user)
            .option(ConnectionFactoryOptions.PASSWORD, password)
            .option(ConnectionFactoryOptions.DATABASE, this.database)
            .option(ConnectionFactoryOptions.PROTOCOL, protocol)
            .option(ConnectionFactoryOptions.HOST, host)
            .option(ConnectionFactoryOptions.PORT, port)
            .build(),
    )
}
