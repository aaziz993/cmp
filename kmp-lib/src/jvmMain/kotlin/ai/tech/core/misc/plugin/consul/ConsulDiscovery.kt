package ai.tech.core.misc.plugin.consul

import ai.tech.core.misc.consul.client.agent.model.Check
import ai.tech.core.misc.consul.client.agent.model.Registration
import ai.tech.core.misc.consul.model.config.Discovery
import ai.tech.core.misc.consul.server.plugin.ConsulDiscovery
import ai.tech.core.misc.model.config.EnabledConfig
import io.ktor.client.HttpClient
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.utils.io.KtorDsl

// Learn more: https://ktor.io/docs/custom-plugins.html#handle-app-events
@KtorDsl
public fun Application.configureConsulDiscovery(
    httpClient: HttpClient,
    address: String,
    config: Discovery?,
    instanceId: String? = null,
    serviceAddress: String,
    tags: List<String> = emptyList(),
) = config?.takeIf(EnabledConfig::enable)?.let {
    val registration =
        Registration(
            it.serviceName,
            it.instanceId ?: instanceId,
            it.tags ?: tags,
            serviceAddress,
            meta = it.meta,
            checks = listOf(
                Check(
                    interval =it.healthCheckInterval,
                    deregisterCriticalServiceAfter = it.healthCheckCriticalTimeout,
                    header = it.header,
                ),
            ),
            enableTagOverride = false,
            serviceWeights = it.serviceWeights,
        )

    install(ConsulDiscovery(httpClient, address, registration))
}
