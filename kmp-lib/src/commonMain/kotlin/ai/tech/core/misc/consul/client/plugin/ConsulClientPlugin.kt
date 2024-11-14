package ai.tech.core.misc.consul.client.plugin

import ai.tech.core.misc.consul.client.ConsulClient
import ai.tech.core.misc.consul.client.agent.model.Registration
import ai.tech.core.misc.consul.model.config.LoadBalancer
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

    val httpClient = client

    client.requestPipeline.intercept(HttpRequestPipeline.Render) {
        val consulClient = ConsulClient(httpClient, address)

        val nodes = consulClient.health.getServiceInstances(config.name)
        val selectedNode = checkNotNull(loadBalancer(nodes)) {
            "Impossible to find available nodes of the ${config.name}"
        }

        context.url {
            selectedNode.service.address host =
            port = selectedNode.service.port
        }

    }
}
