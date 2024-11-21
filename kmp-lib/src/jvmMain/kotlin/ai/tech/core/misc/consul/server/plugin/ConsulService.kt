package ai.tech.core.misc.consul.server.plugin

import ai.tech.core.misc.consul.client.AbstractConsulClient
import ai.tech.core.misc.consul.client.agent.AgentClient
import ai.tech.core.misc.consul.client.agent.model.Registration
import io.ktor.client.HttpClient
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.createApplicationPlugin
import kotlinx.coroutines.runBlocking

@Suppress("FunctionName")
public fun ConsulService(
    httpClient: HttpClient,
    address: String,
    config: Registration,
): ApplicationPlugin<Registration> = createApplicationPlugin(
    "ConsulService",
    { config },
) {
    runBlocking {
        AgentClient(httpClient, address).register(config)
    }
}
