package ai.tech.core.misc.consul.client.plugin

import ai.tech.core.misc.consul.client.health.HealthClient
import ai.tech.core.misc.consul.model.config.LoadBalancer
import ai.tech.core.misc.network.http.client.httpUrl
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequestPipeline

@Suppress("FunctionName")
public fun ConsulDiscovery(
    address: String,
    loadBalancer: LoadBalancer = LoadBalancer.ROUND_ROBIN,
    serviceName: String,
): ClientPlugin<Unit> = createClientPlugin("ConsulDiscovery", { }) {

    val healthClient = HealthClient(client, address)

    client.requestPipeline.intercept(HttpRequestPipeline.Render) {
        val nodes = healthClient.getHealthyServiceInstances(serviceName)

        val selectedNode = checkNotNull(loadBalancer(nodes)) {
            "Impossible to find available instances of the $serviceName"
        }

        context.url {
            host = selectedNode.service.address.httpUrl.host
            port = selectedNode.service.port
        }
    }
}
