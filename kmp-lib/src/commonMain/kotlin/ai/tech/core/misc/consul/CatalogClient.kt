package ai.tech.core.misc.consul

import ai.tech.core.misc.consul.module.CatalogNode
import ai.tech.core.misc.consul.module.CatalogService
import ai.tech.core.misc.consul.module.Node
import ai.tech.core.misc.consul.module.Service
import ai.tech.core.misc.consul.module.ServiceCheck
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonArray

public class CatalogClient internal constructor(private val client: HttpClient) {

    public suspend fun register(
        node: String,
        id: String? = null,
        address: String? = null,
        datacenter: String? = null,
        taggedAddress: Map<String, *>? = null,
        meta: Map<String, String>? = null,
        service: Service? = null,
        interval: String? = null,
        check: ServiceCheck? = null,
        skipNodeUpdate: Boolean? = null,
        ns: String? = null
    ) {
        client.put("${PATH}register") {
            setBody(
                mapOf(
                    "Node" to node,
                    "ID" to id,
                    "Datacenter" to datacenter,
                    "Address" to address,
                    "TaggedAddresses" to taggedAddress,
                    "NodeMeta" to meta,
                    "Service" to service,
                    "Interval" to interval,
                    "Check" to check,
                    "SkipNodeUpdate" to skipNodeUpdate,
                    "ns" to ns,
                ).filterValues { it != null },
            )
        }
    }

    public suspend fun deregister(
        node: String,
        datacenter: String? = null,
        checkId: String? = null,
        serviceId: String? = null,
        namespace: String? = null
    ) {
        client.put("${PATH}register") {
            setBody(
                mapOf(
                    "Node" to node,
                    "Datacenter" to datacenter,
                    "CheckID" to checkId,
                    "ServiceID" to serviceId,
                    "Namespace" to namespace,
                ).filterValues { it != null },
            )
        }
    }

    public suspend fun datacenters(): List<String> = client.get("${PATH}datacenters").body()

    public suspend fun nodes(
        dc: String? = null,
        near: String? = null,
        nodeMeta: String? = null,
        filter: String? = null
    ): List<Node> = client.get("${PATH}nodes") {
        parameter("dc", dc)
        parameter("near", near)
        parameter("node-meta", nodeMeta)
        parameter("filter", filter)
    }.body()

    public suspend fun services(
        dc: String? = null,
        nodeMeta: String? = null,
        ns: String? = null
    ): Map<String, List<String>> = client.get("${PATH}services") {
        parameter("dc", dc)
        parameter("node-meta", nodeMeta)
        parameter("ns", ns)
    }.body()

    public suspend fun service(
        service: String,
        near: String? = null,
        dc: String? = null,
        nodeMeta: String? = null,
        tag: String? = null,
        ns: String? = null,
        filter: String? = null
    ): CatalogService = client.get("${PATH}services/$service") {
        parameter("dc", dc)
        parameter("near", near)
        parameter("tag", tag)
        parameter("node-meta", nodeMeta)
        parameter("ns", ns)
        parameter("filter", filter)
    }.body()

    public suspend fun connectService(service: String): CatalogService = client.get("${PATH}connect/$service").body()

    public suspend fun node(
        node: String,
        dc: String? = null,
        ns: String? = null,
        filter: String? = null
    ): CatalogNode = client.get("${PATH}node/$node") {
        parameter("dc", dc)
        parameter("ns", ns)
        parameter("filter", filter)
    }.body()

    public suspend fun nodeServices(
        node: String,
        dc: String? = null,
        ns: String? = null,
        filter: String? = null
    ): CatalogNode = client.get("${PATH}node-services/$node") {
        parameter("dc", dc)
        parameter("ns", ns)
        parameter("filter", filter)
    }.body()

    public suspend fun gatewayServices(
        gateway: String,
        dc: String? = null,
        ns: String? = null
    ): JsonArray = client.get("${PATH}gateway-services/$gateway") {
        parameter("dc", dc)
        parameter("ns", ns)
    }.body()

    public companion object {

        public const val PATH: String = "/catalog/"
    }
}
