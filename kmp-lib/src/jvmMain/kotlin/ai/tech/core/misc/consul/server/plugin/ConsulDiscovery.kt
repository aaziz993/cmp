package ai.tech.core.misc.consul.server.plugin

import ai.tech.core.misc.consul.client.agent.AgentClient
import ai.tech.core.misc.consul.client.agent.model.Registration
import ai.tech.core.misc.consul.model.config.Discovery
import io.ktor.client.HttpClient
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.createApplicationPlugin
import kotlinx.coroutines.runBlocking

@Suppress("FunctionName")
public fun ConsulDiscovery(
    httpClient: HttpClient,
    address: String,
    config: Registration,
): ApplicationPlugin<Registration> = createApplicationPlugin(
    "ConsulDiscovery",
    { config },
) { runBlocking { AgentClient(httpClient, address).register(config) } }
