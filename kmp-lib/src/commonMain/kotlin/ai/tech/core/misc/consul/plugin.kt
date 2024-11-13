package ai.tech.core.misc.consul

import ai.tech.core.misc.consul.module.config.ConsulConfig
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestPipeline

public fun consulConnectClientPlugin(
    config: ConsulConfig,
): ClientPlugin<ConsulConfig> = createClientPlugin(
    "ConsulPluginClient",
    { config },
) {

    val service = config.service!!

    val httpClient = client

    client.requestPipeline.intercept(HttpRequestPipeline.Render) {
        val consul = Consul(httpClient, config)

        val nodes = consul.agent.healthByName(service.name)
        val selectedNode = checkNotNull(config.loadBalancer(nodes)) {
            "Impossible to find available nodes of the ${service.name}"
        }

        service.address?.let { this.context.url.host = it }
        service.port?.let { this.context.url.port = it }

    }
}
