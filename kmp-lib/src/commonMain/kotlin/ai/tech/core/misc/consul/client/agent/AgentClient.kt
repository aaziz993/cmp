package ai.tech.core.misc.consul.client.agent

import ai.tech.core.misc.consul.client.AbstractConsulClient
import ai.tech.core.misc.consul.client.agent.model.Agent
import ai.tech.core.misc.consul.client.agent.model.Check
import ai.tech.core.misc.consul.client.agent.model.FullService
import ai.tech.core.misc.consul.client.agent.model.Member
import ai.tech.core.misc.consul.client.agent.model.Registration
import ai.tech.core.misc.consul.client.health.model.HealthCheck
import ai.tech.core.misc.consul.client.health.model.Service
import ai.tech.core.misc.consul.client.model.State
import ai.tech.core.misc.consul.model.parameter.QueryParameterParameters
import ai.tech.core.misc.consul.model.parameter.QueryParameters
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient

/**
 * HTTP Client for /v1/agent/ endpoints.
 *
 * @see [The Consul API Docs](http://www.consul.io/docs/agent/http.html.agent)
 */
public class AgentClient(
    httpClient: HttpClient,
    address: String,
    aclToken: String? = null
) : AbstractConsulClient(httpClient, address, aclToken) {
    /**
     * Constructs an instance of this class.
     *
     * @param ktorfit The [Ktorfit] to build a client from.
     */
    private val api: AgentApi = ktorfit.createAgentApi()

    /**
     * Indicates whether a particular service is registered with
     * the local Consul agent.
     *
     * @return `true` if a particular service is registered with
     * the local Consul agent, otherwise `false`.
     */
    public suspend fun isRegistered(serviceId: String): Boolean = getServices().containsKey(serviceId)

    /**
     * Pings the Consul Agent.
     */
    public suspend fun ping(): Unit = api.ping()

    /**
     * Registers the client as a service with Consul.  Registration enables
     * the use of checks.
     *
     * @param registration The registration payload.
     * @param options An optional QueryParameters instance.
     * @param queryParameterParameters The Query Parameter Options to use.
     */
    public suspend fun register(
        registration: Registration,
        queryParameters: QueryParameters = QueryParameters(),
        queryParameterParameters: QueryParameterParameters = QueryParameterParameters()
    ): Unit = api.register(registration, queryParameters.query, queryParameterParameters.queryParameters)

    /**
     * De-register a particular service from the Consul Agent.
     */
    public suspend fun deregister(serviceId: String, queryParameters: QueryParameters = QueryParameters()): Unit = api.deregister(serviceId, queryParameters.query)

    /**
     * Registers a Health Check with the Agent.
     *
     * @param check The Check to register.
     */
    public suspend fun registerCheck(check: Check): Unit = api.registerCheck(check)

    /**
     * De-registers a Health Check with the Agent
     *
     * @param checkId the id of the Check to deregister
     */
    public suspend fun deregisterCheck(checkId: String): Unit = api.deregisterCheck(checkId)

    /**
     * Retrieves the Agent's configuration and member information.
     *
     *
     * GET /v1/agent/self
     *
     * @return The Agent information.
     */
    public suspend fun get(): Agent = api.getSelf()

    /**
     * Retrieves all checks registered with the Agent.
     *
     *
     * GET /v1/agent/checks
     *
     * @param queryParameters The Query Options to use.
     * @return Map of Check ID to Checks.
     */
    public suspend fun getChecks(queryParameters: QueryParameters = QueryParameters()): Map<String, HealthCheck> = api.getChecks(queryParameters.query)

    /**
     * Retrieves all services registered with the Agent.
     *
     *
     * GET /v1/agent/services
     *
     * @param queryParameters The Query Options to use.
     * @return Map of Service ID to Services.
     */
    public suspend fun getServices(queryParameters: QueryParameters = QueryParameters()): Map<String, Service> = api.getServices(queryParameters.query)

    /**
     * Retrieves all information about a service.
     *
     *
     * GET /v1/agent/service/:service_id
     *
     * @param id The service id.
     * @param queryParameters The Query Options to use.
     * @return A [FullService] object.
     */

    public suspend fun getService(id: String, queryParameters: QueryParameters = QueryParameters()): FullService = api.getService(id, queryParameters.query)

    /**
     * Retrieves all members that the Agent can see in the gossip pool.
     *
     *
     * GET /v1/agent/members
     *
     * @param queryParameters The Query Options to use.
     * @return List of Members.
     */
    public suspend fun getMembers(queryParameters: QueryParameters = QueryParameters()): List<Member> = api.getMembers(queryParameters.query)

    /**
     * GET /v1/agent/force-leave/{node}
     *
     *
     * Instructs the agent to force a node into the "left" state.
     *
     * @param node Node name
     * @param queryParameterParameters The Query Parameters Options to use.
     */
    public suspend fun forceLeave(node: String, queryParameterParameters: QueryParameterParameters = QueryParameterParameters()): Unit = api.forceLeave(node, queryParameterParameters.queryParameters)

    /**
     * Checks in with Consul.
     *
     * @param checkId The Check ID to check in.
     * @param state   The current state of the Check.
     * @param note    Any note to associate with the Check.
     */

    public suspend fun check(state: State, checkId: String, queryParameters: QueryParameters = QueryParameters()): Unit = api.check(state.name.lowercase(), checkId, queryParameters.query)

    /**
     * GET /v1/agent/join/{address}`queryParameters`
     *
     * Instructs the agent to join a node.
     *
     * @param address The address to join.
     * @param queryParameters The Query Options to use.
     * @return `true` if successful, otherwise `false`.
     */
    public suspend fun join(address: String, queryParameters: QueryParameters = QueryParameters()): Unit = api.join(address, queryParameters.query)

    /**
     * Toggles maintenance mode for a service ID.
     *
     * @param serviceId The service ID.
     * @param queryParameters The Query Options to use.
     */
    public suspend fun toggleMaintenanceMode(serviceId: String, queryParameters: QueryParameters = QueryParameters()): Unit = api.toggleMaintenanceMode(serviceId, queryParameters.query)
}
