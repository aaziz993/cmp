package ai.tech.core.misc.consul.client.catalog

import ai.tech.core.misc.consul.model.option.QueryOptions
import com.orbitz.consul.async.ConsulResponseCallback
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
     * @param queryOptions
     * @return
     */
    public fun getDatacenters(queryOptions: QueryOptions): List<String> {
        return api.getDatacenters(queryOptions.headers)
    }

    /**
     * Retrieves all nodes.
     *
     *
     * GET /v1/catalog/nodes
     *
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a list of
     * [com.orbitz.consul.model.health.Node] objects.
     */
    public fun getNodes(): ConsulResponse<List<Node>> {
        return getNodes(QueryOptions.BLANK)
    }

    /**
     * Retrieves all nodes for a given datacenter with [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/catalog/nodesdc={datacenter}
     *
     * @param queryOptions The Query Options to use.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a list of
     * [com.orbitz.consul.model.health.Node] objects.
     */
    public fun getNodes(queryOptions: QueryOptions): ConsulResponse<List<Node>> {
        return http.extractConsulResponse(
                api.getNodes(
                        queryOptions.toQuery(),
                        queryOptions.getTag(), queryOptions.getNodeMeta(),
                        queryOptions.headers,
                ),
        )
    }

    /**
     * Asynchronously retrieves the nodes for a given datacenter with [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/catalog/nodesdc={datacenter}
     *
     * @param queryOptions The Query Options to use.
     * @param callback     Callback implemented by callee to handle results.
     * [com.orbitz.consul.model.health.Node] objects.
     */
    public fun getNodes(queryOptions: QueryOptions, callback: ConsulResponseCallback<List<Node>>) {
        http.extractConsulResponse(
                api.getNodes(
                        queryOptions.toQuery(), queryOptions.getTag(),
                        queryOptions.getNodeMeta(), queryOptions.headers,
                ),
                callback,
        )
    }

    /**
     * Retrieves all services for a given datacenter.
     *
     *
     * GET /v1/catalog/servicesdc={datacenter}
     *
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a map of service name to list of tags.
     */
    public fun getServices(): ConsulResponse<Map<String, List<String>>> {
        return getServices(QueryOptions.BLANK)
    }

    /**
     * Asynchronously retrieves the services for a given datacenter.
     *
     *
     * GET /v1/catalog/servicesdc={datacenter}
     *
     * @param callback     Callback implemented by callee to handle results.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a map of service name to list of tags.
     */
    public fun getServices(callback: ConsulResponseCallback<Map<String, List<String>>>) {
        getServices(QueryOptions.BLANK, callback)
    }

    /**
     * Retrieves all services for a given datacenter.
     *
     *
     * GET /v1/catalog/servicesdc={datacenter}
     *
     * @param queryOptions The Query Options to use.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a map of service name to list of tags.
     */
    public fun getServices(queryOptions: QueryOptions): ConsulResponse<Map<String, List<String>>> {
        return http.extractConsulResponse(
                api.getServices(
                        queryOptions.toQuery(),
                        queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers,
                ),
        )
    }

    /**
     * Asynchronously retrieves the services for a given datacenter.
     *
     *
     * GET /v1/catalog/servicesdc={datacenter}
     *
     * @param queryOptions The Query Options to use.
     * @param callback     Callback implemented by callee to handle results.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a map of service name to list of tags.
     */
    public fun getServices(
        queryOptions: QueryOptions,
        callback: ConsulResponseCallback<Map<String, List<String>>>
    ) {
        http.extractConsulResponse(
                api.getServices(
                        queryOptions.toQuery(),
                        queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers,
                ),
                callback,
        )
    }

    /**
     * Retrieves the single service.
     *
     *
     * GET /v1/catalog/service/{service}
     *
     * @return A [com.orbitz.consul.model.ConsulResponse] containing
     * [com.orbitz.consul.model.catalog.CatalogService] objects.
     */
    public fun getService(service: String): ConsulResponse<List<CatalogService>> {
        return getService(service, QueryOptions.BLANK)
    }

    /**
     * Retrieves a single service for a given datacenter with [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/catalog/service/{service}dc={datacenter}
     *
     * @param queryOptions The Query Options to use.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing
     * [com.orbitz.consul.model.catalog.CatalogService] objects.
     */
    public fun getService(service: String, queryOptions: QueryOptions): ConsulResponse<List<CatalogService>> {
        return http.extractConsulResponse(
                api.getService(
                        service, queryOptions.toQuery(),
                        queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers,
                ),
        )
    }

    /**
     * Asynchronously retrieves the single service for a given datacenter with [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/catalog/service/{service}dc={datacenter}
     *
     * @param queryOptions The Query Options to use.
     * @param callback     Callback implemented by callee to handle results.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing
     * [com.orbitz.consul.model.catalog.CatalogService] objects.
     */
    public fun getService(
        service: String,
        queryOptions: QueryOptions,
        callback: ConsulResponseCallback<List<CatalogService>>
    ) {
        http.extractConsulResponse(
                api.getService(
                        service, queryOptions.toQuery(),
                        queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers,
                ),
                callback,
        )
    }

    /**
     * Retrieves a single node.
     *
     *
     * GET /v1/catalog/node/{node}
     *
     * @return A list of matching [com.orbitz.consul.model.catalog.CatalogService] objects.
     */
    public fun getNode(node: String): ConsulResponse<CatalogNode> {
        return getNode(node, QueryOptions.BLANK)
    }

    /**
     * Retrieves a single node for a given datacenter with [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/catalog/node/{node}dc={datacenter}
     *
     * @param queryOptions The Query Options to use.
     * @return A list of matching [com.orbitz.consul.model.catalog.CatalogService] objects.
     */
    public fun getNode(node: String, queryOptions: QueryOptions): ConsulResponse<CatalogNode> {
        return http.extractConsulResponse(
                api.getNode(
                        node, queryOptions.toQuery(),
                        queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers,
                ),
        )
    }

    /**
     * Asynchronously retrieves the single node for a given datacenter with [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/catalog/node/{node}dc={datacenter}
     *
     * @param queryOptions The Query Options to use.
     * @param callback     Callback implemented by callee to handle results.
     */
    public fun getNode(node: String, queryOptions: QueryOptions, callback: ConsulResponseCallback<CatalogNode>) {
        http.extractConsulResponse(
                api.getNode(
                        node, queryOptions.toQuery(),
                        queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers,
                ),
                callback,
        )
    }

    /**
     * Registers a service or node.
     *
     *
     * PUT /v1/catalog/register
     *
     * @param registration A [CatalogRegistration]
     */
    public fun register(registration: CatalogRegistration) {
        register(registration, QueryOptions.BLANK)
    }

    /**
     * Registers a service or node.
     *
     *
     * PUT /v1/catalog/register
     *
     * @param registration A [CatalogRegistration]
     */
    public fun register(registration: CatalogRegistration, options: QueryOptions) {
        api.register(registration, options.toQuery())
    }

    /**
     * Deregisters a service or node.
     *
     *
     * PUT /v1/catalog/deregister
     *
     * @param deregistration A [CatalogDeregistration]
     */
    public fun deregister(deregistration: CatalogDeregistration) {
        deregister(deregistration, QueryOptions.BLANK)
    }

    /**
     * Deregisters a service or node.
     *
     *
     * PUT /v1/catalog/deregister
     *
     * @param deregistration A [CatalogDeregistration]
     */
    public fun deregister(deregistration: CatalogDeregistration, options: QueryOptions) {
        api.deregister(deregistration, options.toQuery())
    }
}
