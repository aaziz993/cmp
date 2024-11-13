package ai.tech.core.misc.plugin.consul

import ai.tech.core.misc.consul.module.config.ConsulConfig
import ai.tech.core.misc.plugin.consul.plugin.ConsulMicroservice
import io.ktor.client.HttpClient
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.utils.io.KtorDsl

// Learn more: https://ktor.io/docs/custom-plugins.html#handle-app-events
@KtorDsl
public fun Application.configureConsul(
    httpClient: HttpClient,
    config: ConsulConfig?,
) = config?.let {
    install(ConsulMicroservice(httpClient, config)) {
        service {
            name = ""
        }
    }
}
