package ai.tech.core.misc.plugin.consul

import ai.tech.core.misc.consul.client.agent.model.Check
import ai.tech.core.misc.consul.client.agent.model.Registration
import ai.tech.core.misc.consul.model.config.Discovery
import ai.tech.core.misc.consul.server.plugin.ConsulDiscovery
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
    instanceId: String? = null,
    serviceAddress: String,
    tags: List<String> = emptyList(),
    oauth: Set<String>? = null,
    database: Set<String>? = null,
    failureBlock: (Exception, attempt: Int) -> Unit = { _, _ -> },
) = config?.takeIf(EnabledConfig::enable)?.let {
    val id = it.instanceId ?: instanceId
    val registration =
        Registration(
            it.serviceName,
            id,
            it.tags ?: tags,
            serviceAddress,
            meta = it.meta,
            checks = listOf(
                Check(
                    interval = it.healthCheckInterval,
                    deregisterCriticalServiceAfter = it.healthCheckCriticalTimeout,
                    header = it.header,
                ),
            ) + oauth?.map { name ->
                Check(
                    name = name,
                    id = "$id:$name",
                    http = "$serviceAddress/$name",
                    interval = it.healthCheckInterval,
                    deregisterCriticalServiceAfter = it.healthCheckCriticalTimeout,
                    header = it.header,
                )
            }.orEmpty() + database?.map { name ->
                Check(
                    name = name,
                    id = "$id:$name",
                    http = "$serviceAddress/$name",
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
