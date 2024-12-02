package ai.tech.core.data.database.r2dbc

import io.r2dbc.spi.ConnectionFactory
import io.r2dbc.spi.ConnectionFactoryOptions

internal class R2dbcConnectionFactory(
    private val delegate: ConnectionFactory,
    public val options: ConnectionFactoryOptions
) : ConnectionFactory by delegate
