package ai.tech.core.misc.consul.server.plugin

import ai.tech.core.misc.consul.client.agent.AgentClient
import ai.tech.core.misc.consul.client.agent.model.Registration
import ai.tech.core.misc.resilience.model.Retry
import io.ktor.client.*
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking

@Suppress("FunctionName")
public fun ConsulDiscovery(
    httpClient: HttpClient,
    address: String,
    config: Registration,
    retry: Retry = Retry(),
    failFast: Boolean = false,
    failureBlock: (Exception, attempt: Int) -> Unit = { _, _ -> }
): ApplicationPlugin<Registration> = createApplicationPlugin(
    "ConsulDiscovery",
    { config },
) {
    runBlocking {
        retry.run { AgentClient(httpClient, address).register(config) }
    }
}
