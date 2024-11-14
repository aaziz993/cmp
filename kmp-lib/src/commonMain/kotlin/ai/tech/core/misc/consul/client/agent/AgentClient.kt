package ai.tech.core.misc.consul.client.agent

import ai.tech.core.misc.consul.client.agent.model.Agent
import ai.tech.core.misc.consul.client.agent.model.Check
import ai.tech.core.misc.consul.client.agent.model.FullService
import ai.tech.core.misc.consul.client.agent.model.Member
import ai.tech.core.misc.consul.client.agent.model.Registration
import ai.tech.core.misc.consul.client.agent.model.Registration.RegCheck
import ai.tech.core.misc.consul.client.health.model.HealthCheck
import ai.tech.core.misc.consul.client.health.model.Service
import ai.tech.core.misc.consul.model.option.QueryOptions
import ai.tech.core.misc.consul.model.option.QueryParameterOptions
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.http.Url
import kotlin.time.Duration

/**
 * HTTP Client for /v1/agent/ endpoints.
 *
 * @see [The Consul API Docs](http://www.consul.io/docs/agent/http.html.agent)
 */
public class AgentClient internal constructor(ktorfit: Ktorfit) {
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
     * @param options An optional QueryOptions instance.
     * @param queryParameterOptions The Query Parameter Options to use.
     */
    public suspend fun register(
        registration: Registration,
        options: QueryOptions = QueryOptions(),
        queryParameterOptions: QueryParameterOptions = QueryParameterOptions()
    ): Unit = api.register(registration, options.query, queryParameterOptions.queryParameters)

    /**
     * De-register a particular service from the Consul Agent.
     */
    public suspend fun deregister(serviceId: String, options: QueryOptions = QueryOptions()): Unit = api.deregister(serviceId, options.query)

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
    public suspend fun getSelf(): Agent = api.getSelf()

    /**
     * Retrieves all checks registered with the Agent.
     *
     *
     * GET /v1/agent/checks
     *
     * @param queryOptions The Query Options to use.
     * @return Map of Check ID to Checks.
     */
    public suspend fun getChecks(queryOptions: QueryOptions = QueryOptions()): Map<String, HealthCheck> = api.getChecks(queryOptions.query)

    /**
     * Retrieves all services registered with the Agent.
     *
     *
     * GET /v1/agent/services
     *
     * @param queryOptions The Query Options to use.
     * @return Map of Service ID to Services.
     */
    public suspend fun getServices(queryOptions: QueryOptions = QueryOptions()): Map<String, Service> = api.getServices(queryOptions.query)

    /**
     * Retrieves all information about a service.
     *
     *
     * GET /v1/agent/service/:service_id
     *
     * @param id           The service id.
     * @param queryOptions The Query Options to use.
     * @return A [com.orbitz.consul.model.ConsulResponse] containing [FullService] object.
     */

    public suspend fun getService(id: String, queryOptions: QueryOptions = QueryOptions()): FullService = api.getService(id, queryOptions.query)

    /**
     * Retrieves all members that the Agent can see in the gossip pool.
     *
     *
     * GET /v1/agent/members
     *
     * @param queryOptions The Query Options to use.
     * @return List of Members.
     */
    public suspend fun getMembers(queryOptions: QueryOptions = QueryOptions()): List<Member> = api.getMembers(queryOptions.query)

    /**
     * GET /v1/agent/force-leave/{node}
     *
     *
     * Instructs the agent to force a node into the "left" state.
     *
     * @param node Node name
     * @param queryParameterOptions The Query Parameters Options to use.
     */
    public suspend fun forceLeave(node: String, queryParameterOptions: QueryParameterOptions = QueryParameterOptions()): Unit = api.forceLeave(node, queryParameterOptions.queryParameters)

    /**
     * Checks in with Consul.
     *
     * @param checkId The Check ID to check in.
     * @param state   The current state of the Check.
     * @param note    Any note to associate with the Check.
     */

    public suspend fun check(state: String, checkId: String, queryOptions: QueryOptions = QueryOptions()): Unit = api.check(state, checkId, queryOptions.query)

    /**
     * GET /v1/agent/join/{address}`queryOptions`
     *
     * Instructs the agent to join a node.
     *
     * @param address The address to join.
     * @param queryOptions The Query Options to use.
     * @return `true` if successful, otherwise `false`.
     */
    public suspend fun join(address: String, queryOptions: QueryOptions = QueryOptions()): Unit = api.join(address, queryOptions.query)

    /**
     * Toggles maintenance mode for a service ID.
     *
     * @param serviceId The service ID.
     * @param queryOptions The Query Options to use.
     */
    public suspend fun toggleMaintenanceMode(serviceId: String, queryOptions: QueryOptions = QueryOptions()): Unit = api.toggleMaintenanceMode(serviceId, queryOptions.query)
}
