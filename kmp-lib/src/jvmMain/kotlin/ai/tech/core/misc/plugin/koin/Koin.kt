package ai.tech.core.misc.plugin.koin

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.model.config.server.ServerConfig
import ai.tech.core.misc.plugin.koin.module.databaseModule
import io.ktor.server.application.*
import org.koin.core.KoinApplication
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

public fun Application.configureKoin(
    config: ServerConfig,
    application: KoinApplication.() -> Unit = {}
) {
    install(Koin) {
        config.host.koin?.takeIf(EnabledConfig::enabled)?.let {
            it.logging?.takeIf(EnabledConfig::enabled)?.level?.let { slf4jLogger(Level.valueOf(it)) } ?: slf4jLogger()
        }

        application()

        modules(
            module { single { config } },
            databaseModule(config.host.database),
        )
    }
}




