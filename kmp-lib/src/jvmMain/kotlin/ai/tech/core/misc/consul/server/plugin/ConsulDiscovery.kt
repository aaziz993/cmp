package ai.tech.core.misc.consul.server.plugin

import ai.tech.core.misc.consul.client.agent.AgentClient
import ai.tech.core.misc.consul.client.agent.model.Registration
import ai.tech.core.misc.util.model.Retry
import ai.tech.core.misc.util.run
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.server.application.*
import kotlinx.coroutines.runBlocking

@Suppress("FunctionName")
public fun ConsulDiscovery(
    httpClient: HttpClient,
    address: String,
    config: Registration,
    retry: Retry? = null,
    failFast: Boolean = false,
    failureBlock: (Exception, attempt: Int) -> Unit = { _, _ -> }
): ApplicationPlugin<Registration> = createApplicationPlugin(
    "ConsulDiscovery",
    { config },
) {
    runBlocking {
        try {
            run(retry, failureBlock) { AgentClient(httpClient, address).register(config) }
        }
        catch (e: HttpRequestTimeoutException) {
            if (failFast) {
                throw e
            }
        }
    }
}
