package ai.tech.core.misc.consul.server.plugin

import ai.tech.core.misc.consul.client.ConsulClient
import ai.tech.core.misc.consul.client.agent.model.Registration
import io.ktor.client.HttpClient
import io.ktor.server.application.ApplicationPlugin
import io.ktor.server.application.createApplicationPlugin
import kotlinx.coroutines.runBlocking

@Suppress("FunctionName")
public fun Consul(
    httpClient: HttpClient,
    address: String,
    config: Registration,
): ApplicationPlugin<Registration> = createApplicationPlugin(
    "Consul",
    { config },
) {
    val consulClient = ConsulClient(httpClient, address)

    runBlocking {
        consulClient.agent.register(config)
    }
}
