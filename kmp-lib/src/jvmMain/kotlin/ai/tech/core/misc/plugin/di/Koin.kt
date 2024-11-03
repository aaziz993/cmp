package ai.tech.core.misc.plugin.di

import core.plugin.di.module.databaseModule
import io.ktor.server.application.*
import org.koin.core.KoinApplication
import org.koin.core.logger.Level
import org.koin.dsl.module
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

public inline fun <reified T:ServerConfig> Application.configKoin(
    config: T,
    crossinline application: KoinApplication.() -> Unit = {}
) {
    install(Koin) {
        config.koin?.let {
            it.logging?.level?.let { slf4jLogger(Level.valueOf(it)) } ?: slf4jLogger()
        }

        application()

        modules(
            module { single { config } },
            databaseModule(config.database)
        )
    }
}




