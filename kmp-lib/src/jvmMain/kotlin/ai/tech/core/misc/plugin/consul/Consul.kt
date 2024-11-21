package ai.tech.core.misc.plugin.consul

import ai.tech.core.misc.consul.model.config.ConsulConfig
import ai.tech.core.misc.consul.server.plugin.ConsulDiscovery
import ai.tech.core.misc.model.config.EnabledConfig
import io.ktor.client.HttpClient
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.application.uninstall
import io.ktor.utils.io.KtorDsl

// Learn more: https://ktor.io/docs/custom-plugins.html#handle-app-events
@KtorDsl
public fun Application.configureConsul(
    httpClient: HttpClient,
    config: ConsulConfig?,
) = config?.takeIf(EnabledConfig::enable)?.discovery?.takeIf(EnabledConfig::enable)?.let {
    install(ConsulDiscovery(httpClient, config.address, serviceAddress, it))
}
