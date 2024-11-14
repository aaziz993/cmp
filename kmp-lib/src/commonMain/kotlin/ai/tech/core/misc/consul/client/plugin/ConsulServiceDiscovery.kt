package ai.tech.core.misc.consul.client.plugin

import ai.tech.core.misc.consul.client.ConsulClient
import ai.tech.core.misc.consul.model.config.LoadBalancer
import ai.tech.core.misc.network.http.client.httpUrl
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestPipeline

@Suppress("FunctionName")
public fun ConsulServiceDiscovery(
    consulAddress: String,
    serviceName: String,
    loadBalancer: LoadBalancer,
): ClientPlugin<Unit> = createClientPlugin("ConsulServiceDiscovery", { }) {

    val consulClient = ConsulClient(client, consulAddress)

    client.requestPipeline.intercept(HttpRequestPipeline.Render) {
        val nodes = consulClient.health.getHealthyServiceInstances(serviceName)

        val selectedNode = checkNotNull(loadBalancer(nodes)) {
            "Impossible to find available nodes of the $serviceName"
        }

        context.url {
            host = selectedNode.service.address.httpUrl.host
            port = selectedNode.service.port
        }
    }
}
