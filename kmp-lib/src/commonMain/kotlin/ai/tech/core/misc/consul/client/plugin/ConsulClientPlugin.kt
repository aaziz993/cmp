package ai.tech.core.misc.consul.client.plugin

import ai.tech.core.misc.consul.client.ConsulClient
import ai.tech.core.misc.consul.client.agent.model.Registration
import ai.tech.core.misc.consul.model.config.LoadBalancer
import ai.tech.core.misc.network.http.client.httpUrl
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestPipeline

@Suppress("FunctionName")
public fun ConsulClientPlugin(
    address: String,
    consulAddress: String,
    config: Registration,
    loadBalancer: LoadBalancer,
): ClientPlugin<Registration> = createClientPlugin(
    "ConsulClientPlugin",
    { config },
) {

    val consulClient = ConsulClient(client, address)

    client.requestPipeline.intercept(HttpRequestPipeline.Render) {

        val nodes = consulClient.health.getHealthyServiceInstances(config.name)
        val selectedNode = checkNotNull(loadBalancer(nodes)) {
            "Impossible to find available nodes of the ${config.name}"
        }

        val serviceHost = selectedNode.service.address.httpUrl.host
        this.context.url.host = serviceHost
        this.context.url.port = selectedNode.service.port

    }
}
