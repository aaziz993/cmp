package ai.tech

import ai.tech.core.data.filesystem.readResourceText
import ai.tech.core.misc.config.ConfigService
import ai.tech.core.misc.model.config.server.ServerConfigImpl
import ai.tech.core.misc.plugin.configure
import ai.tech.di.ServerModule
import ai.tech.map.mapRouting
import arrow.continuations.SuspendApp
import arrow.continuations.ktor.server
import arrow.fx.coroutines.resourceScope
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.*
import kotlinx.coroutines.awaitCancellation
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import org.koin.ktor.ext.get

@OptIn(InternalSerializationApi::class)
public fun main(args: Array<String>): Unit = SuspendApp {
    resourceScope {
        val serverConfig = ConfigService<ServerConfigImpl>(ServerConfigImpl::class.serializer()) { readResourceText(it) }.readConfig()

        server(Netty, watchPaths = serverConfig.host.watchPaths, configure = { configure(serverConfig.host) }) { module(serverConfig) }

        awaitCancellation()
    }
}


@Suppress("unused")
public fun Application.module(config: ServerConfigImpl) = configure(
    config,
    { ServerModule().module },
    routingBlock = {
        // Add all other routes here
        mapRouting(get())
    },
)


