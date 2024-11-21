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
    consulAddress: String,
    serviceAddress: String,
    config: Discovery,
): ApplicationPlugin<Discovery> = createApplicationPlugin(
    "ConsulDiscovery",
    { config },
) {
    runBlocking {
        AgentClient(httpClient, consulAddress).register(
            Registration(
                config.serviceName,
                config.instanceId ?: config.serviceName,
                tags = config.tags,
                meta = config.meta,
                checks = listOf(
                    Registration.RegCheck(
                        serviceAddress,
                        interval = "${config.healthCheckInterval.inWholeMilliseconds}ms",
                        deregisterCriticalServiceAfter = config.healthCheckCriticalTimeout?.inWholeMilliseconds?.let { "${it}ms" },
                        header = config.header,
                    ),
                ),
                enableTagOverride = false,
            ),
        )
    }
}
