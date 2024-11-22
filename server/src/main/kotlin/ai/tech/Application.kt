package ai.tech

import ai.tech.core.data.filesystem.readResourceText
import ai.tech.core.misc.config.ConfigService
import ai.tech.core.misc.model.config.server.keyStore
import ai.tech.core.misc.plugin.configure
import ai.tech.di.ServerModule
import ai.tech.map.mapRouting
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import java.io.File
import java.util.concurrent.TimeUnit
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.serializer
import org.koin.ktor.ext.get
import org.slf4j.LoggerFactory
import ai.tech.core.misc.model.config.server.ServerConfigImpl
import org.koin.ksp.generated.*

@OptIn(InternalSerializationApi::class)
public suspend fun main(args: Array<String>) {
    val serverConfig = ConfigService<ServerConfigImpl>(ServerConfigImpl::class.serializer()) { readResourceText(it) }.readConfig()

    val server = embeddedServer(
        Netty,
        applicationEnvironment { log = LoggerFactory.getLogger("ktor.application") },
        { envConfig(serverConfig) },
    ) { module(serverConfig) }.start(false)

    Runtime.getRuntime().addShutdownHook(
        Thread {
            server.stop(1, 5, TimeUnit.SECONDS)
        },
    )
    Thread.currentThread().join()
}

private fun ApplicationEngine.Configuration.envConfig(config: ServerConfigImpl) {
    connector {
        port = config.ktorServer.port
    }

    config.ktorServer.ssl?.let {
        sslConnector(
            keyStore = it.keyStore,
            keyAlias = it.keyAlias,
            keyStorePassword = { it.keyStorePassword.toCharArray() },
            privateKeyPassword = { it.privateKeyPassword.toCharArray() },
        ) {
            port = it.port
            keyStorePath = File(it.keyStorePath)
        }
    }
}

@Suppress("unused")
public fun Application.module (config: ServerConfigImpl) = configure(
    config,
    { ServerModule().module },
    routingBlock = {
        // Add all other routes here
        mapRouting(get())
    },
)


