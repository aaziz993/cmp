package ai.tech.core.misc.consul.client.plugin

import ai.tech.core.misc.consul.client.Consul
import ai.tech.core.misc.consul.client.model.config.ConsulConfig
import ai.tech.core.misc.consul.client.model.config.ConsulPluginServiceConfig
import ai.tech.core.misc.consul.client.model.config.LoadBalancer
import ai.tech.core.misc.consul.client.model.config.takeFirstHealthy
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestPipeline

@Suppress("FunctionName")
public fun ConsulClientPlugin(
    address: String,
    config: ConsulPluginServiceConfig,

): ClientPlugin<ConsulPluginServiceConfig> = createClientPlugin(
    "ConsulClientPlugin",
    { config },
) {

    val httpClient = client

    client.requestPipeline.intercept(HttpRequestPipeline.Render) {
        val consul = Consul(httpClient, address)

        val nodes = consul.agent.healthByName(config.name).values
        val selectedNode = checkNotNull(config.loadBalancer(nodes)) {
            "Impossible to find available nodes of the ${config.name}"
        }

        config.address?.let { this.context.url.host = it }
        config.port?.let { this.context.url.port = it }

    }
}
