package ai.tech.core.misc.plugin.consul

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
    consulAddress: String,
    config: Discovery?,
    instanceId: String? = null,
    tags: List<String> = emptyList(),
    serviceAddress: String,
) = config?.takeIf(EnabledConfig::enable)?.let {
    val registration =
        Registration(
            it.serviceName,
            it.instanceId ?: instanceId,
            tags = it.tags.ifEmpty { tags },
            meta = it.meta,
            checks = listOf(
                Registration.RegCheck(
                    serviceAddress,
                    interval = "${it.healthCheckInterval.inWholeMilliseconds}ms",
                    deregisterCriticalServiceAfter = it.healthCheckCriticalTimeout?.inWholeMilliseconds?.let { "${it}ms" },
                    header = it.header,
                ),
            ),
            enableTagOverride = false,
        )

    install(ConsulDiscovery(httpClient, consulAddress, registration))
}
