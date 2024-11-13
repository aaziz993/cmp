package ai.tech.core.misc.consul.client.agent

import ai.tech.core.misc.consul.client.agent.model.Agent
import ai.tech.core.misc.consul.client.agent.model.Check
import ai.tech.core.misc.consul.client.agent.model.FullService
import ai.tech.core.misc.consul.client.agent.model.Member
import ai.tech.core.misc.consul.client.agent.model.Registration
import ai.tech.core.misc.consul.client.health.model.HealthCheck
import ai.tech.core.misc.consul.client.health.model.Service
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.QueryName

public interface AgentApi {

    @PUT("agent/service/register")
    public suspend fun register(
        @Body registration: Registration,
        @QueryMap options: Map<String, String>,
        @QueryName optionsParameters: List<String>
    )

    @PUT("agent/service/deregister/{serviceId}")
    public suspend fun deregister(
        @Path("serviceId") serviceId: String,
        @QueryMap options: Map<String, String>
    )

    @PUT("agent/check/register")
    public suspend fun registerCheck(@Body check: Check)

    @PUT("agent/check/deregister/{checkId}")
    public suspend fun deregisterCheck(@Path("checkId") checkId: String)

    @GET("status/leader")
    public suspend fun ping()

    @GET("agent/self")
    public suspend fun getSelf(): Agent

    @GET("agent/checks")
    public suspend fun getChecks(@QueryMap optionsParameters: Map<String, String>): Map<String, HealthCheck>

    @GET("agent/services")
    public suspend fun getServices(@QueryMap query: Map<String, String>): Map<String, Service>

    @GET("agent/service/{serviceId}")
    public suspend fun getService(
        @Path("serviceId") id: String,
        @QueryMap query: Map<String, String>
    ): FullService

    @GET("agent/members")
    public suspend fun getMembers(@QueryMap query: Map<String, String>): List<Member>

    @PUT("agent/force-leave/{node}")
    public suspend fun forceLeave(
        @Path("node") node: String,
        @QueryName optionsParameters: List<String>
    )

    @PUT("agent/check/{state}/{checkId}")
    public suspend fun check(
        @Path("state") state: String,
        @Path("checkId") checkId: String,
        @QueryMap query: Map<String, String>
    )

    @PUT("agent/join/{address}")
    public suspend fun join(
        @Path("address") address: String,
        @QueryMap query: Map<String, String>
    )

    @PUT("agent/service/maintenance/{serviceId}")
    public suspend fun toggleMaintenanceMode(
        @Path("serviceId") serviceId: String,
        @QueryMap query: Map<String, String>
    )
}
