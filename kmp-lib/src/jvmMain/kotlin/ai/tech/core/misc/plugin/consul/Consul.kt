package ai.tech.core.misc.plugin.consul

import ai.tech.core.misc.consul.model.config.ConsulConfig
import ai.tech.core.misc.consul.server.plugin.ConsulService
import ai.tech.core.misc.model.config.EnabledConfig
import io.ktor.client.HttpClient
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.utils.io.KtorDsl

// Learn more: https://ktor.io/docs/custom-plugins.html#handle-app-events
@KtorDsl
public fun Application.configureConsul(
    httpClient: HttpClient,
    config: ConsulConfig?,
) = config?.takeIf(EnabledConfig::enable)?.registration?.let {
    install(ConsulService(httpClient, config.address, it))
}
