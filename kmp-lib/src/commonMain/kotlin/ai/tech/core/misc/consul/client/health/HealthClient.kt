package ai.tech.core.misc.consul.client.health

import com.google.common.collect.ImmutableMap
import de.jensklingenberg.ktorfit.Ktorfit

/**
 * HTTP Client for /v1/health/ endpoints.
 */
public class HealthClient internal constructor(ktorfit: Ktorfit){
    /**
     * Constructs an instance of this class.
     *
     * @param retrofit The [Retrofit] to build a client from.
     */
    private val api: HealthApi = ktorfit.createHealthApi()

    /**
     * Retrieves the healthchecks for a node.
     *
     *
     * GET /v1/health/node/{node}
     *
     * @param node The node to return checks for
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a list of
     * [com.orbitz.consul.model.health.HealthCheck] objects.
     */
    public suspend fun getNodeChecks(node: String): ConsulResponse<List<HealthCheck>> {
        return getNodeChecks(node, QueryOptions.BLANK)
    }

    /**
     * Retrieves the healthchecks for a node in a given datacenter with [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/health/node/{node}dc={datacenter}
     *
     * @param node         The node to return checks for
     * @param queryOptions The Query Options to use.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a list of
     * [com.orbitz.consul.model.health.HealthCheck] objects.
     */
    public suspend fun getNodeChecks(
        node: String,
        queryOptions: QueryOptions
    ): ConsulResponse<List<HealthCheck>> {
        return http.extractConsulResponse(
            api.getNodeChecks(
                node, queryOptions.toQuery(),
                queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers
            )
        )
    }

    /**
     * Retrieves the healthchecks for a service.
     *
     *
     * GET /v1/health/checks/{service}
     *
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a list of
     * [com.orbitz.consul.model.health.HealthCheck] objects.
     */
    public suspend fun getServiceChecks(service: String): ConsulResponse<List<HealthCheck>> {
        return getServiceChecks(service, QueryOptions.BLANK)
    }

    /**
     * Retrieves the healthchecks for a service in a given datacenter with [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/health/checks/{service}dc={datacenter}
     *
     * @param queryOptions The Query Options to use.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a list of
     * [com.orbitz.consul.model.health.HealthCheck] objects.
     */
    public suspend fun getServiceChecks(
        service: String,
        queryOptions: QueryOptions
    ): ConsulResponse<List<HealthCheck>> {
        return http.extractConsulResponse(
            api.getServiceChecks(
                service, queryOptions.toQuery(),
                queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers
            )
        )
    }

    /**
     * Asynchronously retrieves the healthchecks for a service in a given
     * datacenter with [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/health/checks/{service}dc={datacenter}
     *
     * @param service      The service to query.
     * @param queryOptions The Query Options to use.
     * @param callback     Callback implemented by callee to handle results.
     * [com.orbitz.consul.model.health.HealthCheck] objects.
     */
    public suspend fun getServiceChecks(
        service: String,
        queryOptions: QueryOptions,
        callback: ConsulResponseCallback<List<HealthCheck>>
    ) {
        http.extractConsulResponse(
            api.getServiceChecks(
                service, queryOptions.toQuery(),
                queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers
            ), callback
        )
    }

    /**
     * Retrieves the healthchecks for a state.
     *
     *
     * GET /v1/health/state/{state}
     *
     * @param state The state to query.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a list of
     * [com.orbitz.consul.model.health.HealthCheck] objects.
     */
    public suspend fun getChecksByState(state: State): ConsulResponse<List<HealthCheck>> {
        return getChecksByState(state, QueryOptions.BLANK)
    }

    /**
     * Retrieves the healthchecks for a state in a given datacenter with [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/health/state/{state}dc={datacenter}
     *
     * @param state        The state to query.
     * @param queryOptions The Query Options to use.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a list of
     * [com.orbitz.consul.model.health.HealthCheck] objects.
     */
    public suspend fun getChecksByState(
        state: State,
        queryOptions: QueryOptions
    ): ConsulResponse<List<HealthCheck>> {
        return http.extractConsulResponse(
            api.getChecksByState(
                state.getName(), queryOptions.toQuery(),
                queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers
            )
        )
    }

    /**
     * Asynchronously retrieves the healthchecks for a state in a given datacenter with [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/health/state/{state}dc={datacenter}
     *
     * @param state        The state to query.
     * @param queryOptions The Query Options to use.
     * @param callback     Callback implemented by callee to handle results.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a list of
     * [com.orbitz.consul.model.health.HealthCheck] objects.
     */
    public suspend fun getChecksByState(
        state: State, queryOptions: QueryOptions,
        callback: ConsulResponseCallback<List<HealthCheck>>
    ) {
        http.extractConsulResponse(
            api.getChecksByState(
                state.getName(), queryOptions.toQuery(),
                queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers
            ), callback
        )
    }

    /**
     * Retrieves the healthchecks for all healthy service instances.
     *
     *
     * GET /v1/health/service/{service}passing
     *
     * @param service The service to query.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a list of
     * [com.orbitz.consul.model.health.HealthCheck] objects.
     */
    public suspend fun getHealthyServiceInstances(service: String): ConsulResponse<List<ServiceHealth>> {
        return getHealthyServiceInstances(service, QueryOptions.BLANK)
    }

    /**
     * Retrieves the healthchecks for all healthy service instances in a given datacenter with
     * [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/health/service/{service}dc={datacenter}&amp;passing
     *
     * @param service      The service to query.
     * @param queryOptions The Query Options to use.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a list of
     * [com.orbitz.consul.model.health.HealthCheck] objects.
     */
    public suspend fun getHealthyServiceInstances(
        service: String,
        queryOptions: QueryOptions
    ): ConsulResponse<List<ServiceHealth>> {
        return http.extractConsulResponse(
            api.getServiceInstances(
                service,
                HealthClient.Companion.optionsFrom(
                    ImmutableMap.of("passing", "true"),
                    queryOptions.toQuery()
                ),
                queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers
            )
        )
    }


    /**
     * Asynchronously retrieves the healthchecks for all healthy service instances in a given
     * datacenter with [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/health/service/{service}dc={datacenter}&amp;passing
     *
     *
     * Experimental.
     *
     * @param service      The service to query.
     * @param queryOptions The Query Options to use.
     * @param callback     Callback implemented by callee to handle results.
     */
    public suspend fun getHealthyServiceInstances(
        service: String, queryOptions: QueryOptions,
        callback: ConsulResponseCallback<List<ServiceHealth>>
    ) {
        http.extractConsulResponse(
            api.getServiceInstances(
                service,
                HealthClient.Companion.optionsFrom(
                    ImmutableMap.of("passing", "true"),
                    queryOptions.toQuery()
                ),
                queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers
            ), callback
        )
    }

    /**
     * Retrieves the healthchecks for all nodes.
     *
     *
     * GET /v1/health/service/{service}
     *
     * @param service The service to query.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a list of
     * [com.orbitz.consul.model.health.HealthCheck] objects.
     */
    public suspend fun getAllServiceInstances(service: String): ConsulResponse<List<ServiceHealth>> {
        return getAllServiceInstances(service, QueryOptions.BLANK)
    }

    /**
     * Retrieves the healthchecks for all nodes in a given datacenter with
     * [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/health/service/{service}dc={datacenter}
     *
     * @param service      The service to query.
     * @param queryOptions The Query Options to use.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing a list of
     * [com.orbitz.consul.model.health.HealthCheck] objects.
     */
    public suspend fun getAllServiceInstances(
        service: String,
        queryOptions: QueryOptions
    ): ConsulResponse<List<ServiceHealth>> {
        return http.extractConsulResponse(
            api.getServiceInstances(
                service, queryOptions.toQuery(),
                queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers
            )
        )
    }

    /**
     * Asynchronously retrieves the healthchecks for all nodes in a given
     * datacenter with [com.orbitz.consul.option.QueryOptions].
     *
     *
     * GET /v1/health/service/{service}dc={datacenter}
     *
     *
     * Experimental.
     *
     * @param service      The service to query.
     * @param queryOptions The Query Options to use.
     * @param callback     Callback implemented by callee to handle results.
     */
    public suspend fun getAllServiceInstances(
        service: String, queryOptions: QueryOptions,
        callback: ConsulResponseCallback<List<ServiceHealth>>
    ) {
        http.extractConsulResponse(
            api.getServiceInstances(
                service, queryOptions.toQuery(),
                queryOptions.getTag(), queryOptions.getNodeMeta(), queryOptions.headers
            ), callback
        )
    }
}
