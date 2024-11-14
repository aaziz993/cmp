package ai.tech.core.misc.consul.client.catalog

import ai.tech.core.misc.consul.client.catalog.model.CatalogDeregistration
import ai.tech.core.misc.consul.client.catalog.model.CatalogNode
import ai.tech.core.misc.consul.client.catalog.model.CatalogRegistration
import ai.tech.core.misc.consul.client.catalog.model.CatalogService
import ai.tech.core.misc.consul.client.health.model.Node
import ai.tech.core.misc.consul.model.option.QueryParameters
import de.jensklingenberg.ktorfit.Ktorfit

/**
 * HTTP Client for /v1/catalog/ endpoints.
 */
public class CatalogClient internal constructor(ktorfit: Ktorfit) {

    /**
     * Constructs an instance of this class.
     *
     * @param ktorfit The [Ktorfit] to build a client from.
     */
    private val api: CatalogApi = ktorfit.createCatalogApi()

    /**
     * Get the list of datacenters with query options
     * @param queryParameters
     * @return
     */
    public suspend fun getDatacenters(queryParameters: QueryParameters = QueryParameters()): List<String> = api.getDatacenters(queryParameters.headers)

    /**
     * Retrieves all nodes for a given datacenter with [QueryParameters].
     *
     *
     * GET /v1/catalog/nodesdc={datacenter}
     *
     * @param queryParameters The Query Options to use.
     * @return A [List] containing [Node] objects.
     */
    public suspend fun getNodes(queryParameters: QueryParameters = QueryParameters()): List<Node> = api.getNodes(
        queryParameters.query,
        queryParameters.tag.orEmpty(),
        queryParameters.nodeMeta.orEmpty(),
        queryParameters.headers,
    )

    /**
     * Retrieves all services for a given datacenter.
     *
     *
     * GET /v1/catalog/servicesdc={datacenter}
     *
     * @param queryParameters The Query Options to use.
     * @return A [Map] containing service name to list of tags.
     */
    public suspend fun getServices(queryParameters: QueryParameters = QueryParameters()): Map<String, List<String>> =
        api.getServices(
            queryParameters.query,
            queryParameters.tag.orEmpty(), queryParameters.nodeMeta.orEmpty(),
            queryParameters.headers,
        )

    /**
     * Retrieves a single service for a given datacenter with [QueryParameters].
     *
     *
     * GET /v1/catalog/service/{service}dc={datacenter}
     *
     * @param queryParameters The Query Options to use.
     * @return A [List] containing
     * [CatalogService] objects.
     */
    public suspend fun getService(service: String, queryParameters: QueryParameters = QueryParameters()): List<CatalogService> =
        api.getService(
            service,
            queryParameters.query,
            queryParameters.tag.orEmpty(),
            queryParameters.nodeMeta.orEmpty(),
            queryParameters.headers,
        )

    /**
     * Retrieves a single node for a given datacenter with [QueryParameters].
     *
     *
     * GET /v1/catalog/node/{node}dc={datacenter}
     *
     * @param queryParameters The Query Options to use.
     * @return A [CatalogService] object.
     */
    public suspend fun getNode(node: String, queryParameters: QueryParameters = QueryParameters()): CatalogNode =
        api.getNode(
            node,
            queryParameters.query,
            queryParameters.tag.orEmpty(),
            queryParameters.nodeMeta.orEmpty(),
            queryParameters.headers,
        )

    /**
     * Registers a service or node.
     *
     *
     * PUT /v1/catalog/register
     *
     * @param registration A [CatalogRegistration]
     */
    public suspend fun register(registration: CatalogRegistration, queryParameters: QueryParameters = QueryParameters()): Unit = api.register(registration, queryParameters.query)

    /**
     * Deregisters a service or node.
     *
     *
     * PUT /v1/catalog/deregister
     *
     * @param deregistration A [CatalogDeregistration]
     */
    public suspend fun deregister(deregistration: CatalogDeregistration, queryParameters: QueryParameters = QueryParameters()): Unit = api.deregister(deregistration, queryParameters.query)
}
