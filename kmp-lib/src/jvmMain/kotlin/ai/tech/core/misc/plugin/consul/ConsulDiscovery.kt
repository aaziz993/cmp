package ai.tech.core.misc.plugin.consul

import ai.tech.core.misc.consul.client.agent.model.Check
import ai.tech.core.misc.consul.client.agent.model.Registration
import ai.tech.core.misc.consul.model.config.Discovery
import ai.tech.core.misc.consul.server.plugin.ConsulDiscovery
import ai.tech.core.misc.model.config.ApplicationConfig
import ai.tech.core.misc.model.config.EnabledConfig
import io.ktor.client.*
import io.ktor.server.application.*
import io.ktor.utils.io.*

// Learn more: https://ktor.io/docs/custom-plugins.html#handle-app-events
@KtorDsl
public fun Application.configureConsulDiscovery(
    httpClient: HttpClient,
    address: String,
    config: Discovery?,
    serviceAddress: String,
    servicePort: Int? = null,
    applicationConfig: ApplicationConfig? = null,
    healthChecks: Map<String, String>? = null,
    failureBlock: (Exception, attempt: Int) -> Unit = { _, _ -> },
) = config?.takeIf(EnabledConfig::enable)?.let {
    val id = it.instanceId
        ?: applicationConfig?.let { "${it.name}:${it.environment}${servicePort?.let { ":$it" }.orEmpty()}" }

    val registration =
        Registration(
            id,
            it.serviceName,
            serviceAddress,
            servicePort,
            it.tags ?: applicationConfig?.configurations,
            meta = it.meta,
            checks = listOf(
                Check(
                    interval = it.healthCheckInterval,
                    deregisterCriticalServiceAfter = it.healthCheckCriticalTimeout,
                    header = it.header,
                ),
            ) + healthChecks?.map { (name, route) ->
                Check(
                    "$id:$name",
                    name,
                    http = "$serviceAddress/$route",
                    interval = it.healthCheckInterval,
                    deregisterCriticalServiceAfter = it.healthCheckCriticalTimeout,
                    header = it.header,
                )
            }.orEmpty(),
            enableTagOverride = false,
            serviceWeights = it.serviceWeights,
        )

    install(ConsulDiscovery(httpClient, address, registration, it.retry, it.failFast, failureBlock))
}
