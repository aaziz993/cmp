package ai.tech.plugin.di

import io.ktor.server.application.*
import org.koin.ktor.plugin.Koin
import org.koin.logger.slf4jLogger

public fun Application.configureKoin() {
    install(Koin) {
        slf4jLogger()

        modules()
    }
}
