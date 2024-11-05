package ai.tech

import ai.tech.core.data.filesystem.readResourceText
import ai.tech.core.misc.model.config.ServerConfig
import ai.tech.core.misc.plugin.configure
import ai.tech.core.misc.plugin.di.configureKoin
import ai.tech.plugin.di.DefaultModule
import ai.tech.plugin.routing
import io.ktor.network.tls.certificates.*
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import org.koin.ksp.generated.*
import org.koin.ktor.ext.get

public fun main(args: Array<String>) {
    // Configure ssl certificate generation
//        val properties= Properties.dec readResourceText("application")
//        rootConfig.environment.config.decode<GenerateServerSSLConfig>("")?.generate()
    EngineMain.main(args)
}

@Suppress("unused")
public fun Application.module() {
    configureKoin(ServerConfig.read<ServerConfig> { readResourceText(it) }) {
        DefaultModule().module
    }

    val config: ServerConfig = get()

    configure(
        config,
        routingBlock = { routing(config) },
    )
}


