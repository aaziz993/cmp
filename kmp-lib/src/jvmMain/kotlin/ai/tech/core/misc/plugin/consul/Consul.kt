package ai.tech.core.misc.plugin.consul

import ai.tech.core.misc.consul.Consul
import ai.tech.core.misc.model.config.server.ServerConfig
import io.ktor.client.HttpClient
import io.ktor.server.application.Application
import kotlinx.coroutines.runBlocking

public fun Application.configureConsul(
    httpClient: HttpClient,
    config: ServerConfig
) = config.consul?.let {
    val consul = Consul(httpClient, it)

    runBlocking {
        consul.agent.register(config.project, address = config.ktor.deployment.httpURL)
    }
}
