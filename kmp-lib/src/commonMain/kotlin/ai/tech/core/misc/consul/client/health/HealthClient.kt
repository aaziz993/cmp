package ai.tech.core.misc.consul.client.health

import ai.tech.core.misc.consul.client.AbstractConsulClient
import ai.tech.core.misc.consul.client.health.model.HealthCheck
import ai.tech.core.misc.consul.client.health.model.ServiceHealth
import ai.tech.core.misc.consul.client.model.State
import ai.tech.core.misc.consul.model.parameter.QueryParameters
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient

/**
 * HTTP Client for /v1/health/ endpoints.
 */
public class HealthClient(
    httpClient: HttpClient,
    address: String,
    aclToken: String? = null
) : AbstractConsulClient(httpClient, address, aclToken) {

    /**
     * Constructs an instance of this class.
     *
     * @param ktorfit The [Ktorfit] to build a client from.
     */
    private val api: HealthApi = ktorfit.createHealthApi()

    /**
     * Retrieves the healthchecks for a node in a given datacenter with [QueryParameters].
     *
     *
     * GET /v1/health/node/{node}dc={datacenter}
     *
     * @param node         The node to return checks for
     * @param queryParameters The Query Options to use.
     * @return A [List] containing a list of
     * [HealthCheck] objects.
     */
    public suspend fun getNodeChecks(
        node: String,
        queryParameters: QueryParameters = QueryParameters()
    ): List<HealthCheck> =
        api.getNodeChecks(
            node,
            queryParameters.query,
            queryParameters.tag.orEmpty(),
            queryParameters.nodeMeta.orEmpty(),
            queryParameters.headers,
        )

    /**
     * Retrieves the healthchecks for a service in a given datacenter with [QueryParameters].
     *
     *
     * GET /v1/health/checks/{service}dc={datacenter}
     *
     * @param queryParameters The Query Options to use.
     * @return A [List] containing a list of
     * [HealthCheck] objects.
     */
    public suspend fun getServiceChecks(
        service: String,
        queryParameters: QueryParameters = QueryParameters()
    ): List<HealthCheck> =
        api.getServiceChecks(
            service,
            queryParameters.query,
            queryParameters.tag.orEmpty(),
            queryParameters.nodeMeta.orEmpty(),
            queryParameters.headers,
        )

    /**
     * Retrieves the healthchecks for a state in a given datacenter with [QueryParameters].
     *
     *
     * GET /v1/health/state/{state}dc={datacenter}
     *
     * @param state        The state to query.
     * @param queryParameters The Query Options to use.
     * @return A [List] containing a list of
     * [HealthCheck] objects.
     */
    public suspend fun getChecksByState(
        state: State,
        queryParameters: QueryParameters = QueryParameters()
    ): List<HealthCheck> =
        api.getChecksByState(
            state.name.lowercase(),
            queryParameters.query,
            queryParameters.tag.orEmpty(),
            queryParameters.nodeMeta.orEmpty(),
            queryParameters.headers,
        )

    /**
     * Retrieves the healthchecks for all healthy service instances in a given datacenter with
     * [QueryParameters].
     *
     *
     * GET /v1/health/service/{service}dc={datacenter}&amp;passing
     *
     * @param service      The service to query.
     * @param queryParameters The Query Options to use.
     * @return A [List] containing a list of
     * [ServiceHealth] objects.
     */
    public suspend fun getHealthyServiceInstances(
        service: String,
        queryParameters: QueryParameters = QueryParameters()
    ): List<ServiceHealth> =
        api.getServiceInstances(
            service,
            queryParameters.query + mapOf("passing" to "true"),
            queryParameters.tag.orEmpty(),
            queryParameters.nodeMeta.orEmpty(),
            queryParameters.headers,
        )

    /**
     * Retrieves the healthchecks for all nodes in a given datacenter with
     * [QueryParameters].
     *
     *
     * GET /v1/health/service/{service}dc={datacenter}
     *
     * @param service      The service to query.
     * @param queryParameters The Query Options to use.
     * @return A [List] containing a list of
     * [ServiceHealth] objects.
     */
    public suspend fun getAllServiceInstances(
        service: String,
        queryParameters: QueryParameters = QueryParameters()
    ): List<ServiceHealth> =
        api.getServiceInstances(
            service,
            queryParameters.query,
            queryParameters.tag.orEmpty(),
            queryParameters.nodeMeta.orEmpty(),
            queryParameters.headers,
        )
}
