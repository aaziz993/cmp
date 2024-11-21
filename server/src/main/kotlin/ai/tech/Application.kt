package ai.tech

import ai.tech.core.misc.plugin.configure
import ai.tech.di.ServerModule
import ai.tech.map.mapRouting
import io.ktor.server.application.*
import io.ktor.server.netty.*
import org.koin.ktor.ext.get

public fun main(args: Array<String>) {
    // Configure ssl certificate generation
//        val properties= Properties.dec readResourceText("application")
//        rootConfig.environment.config.decode<GenerateServerSSLConfig>("")?.generate()
    EngineMain.main(args)
}

@Suppress("unused")
public fun Application.module() = configure(
    { ServerModule().module },
    routingBlock = {
        // Add all other routes here
        mapRouting(get())
    },
)


