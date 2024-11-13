package ai.tech.core.misc.consul.client.agent

import ai.tech.core.misc.consul.client.agent.model.Agent
import ai.tech.core.misc.consul.client.agent.model.Check
import ai.tech.core.misc.consul.client.agent.model.FullService
import ai.tech.core.misc.consul.client.agent.model.Member
import ai.tech.core.misc.consul.client.agent.model.Registration
import ai.tech.core.misc.consul.client.health.model.HealthCheck
import ai.tech.core.misc.consul.client.health.model.Service
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.QueryName

internal interface AgentClient {

    @PUT("agent/service/register")
    suspend fun register(
        @Body registration: Registration,
        @QueryMap options: Map<String, String>,
        @QueryName optionsParameters: List<String>
    )

    @PUT("agent/service/deregister/{serviceId}")
    suspend fun deregister(
        @Path("serviceId") serviceId: String,
        @QueryMap options: Map<String, String>
    )

    @PUT("agent/check/register")
    suspend fun registerCheck(@Body check: Check)

    @PUT("agent/check/deregister/{checkId}")
    suspend fun deregisterCheck(@Path("checkId") checkId: String)

    @GET("status/leader")
    suspend fun ping()

    @GET("agent/self")
    suspend fun getSelf(): Agent

    @GET("agent/checks")
    suspend fun getChecks(@QueryMap optionsParameters: Map<String, String>): Map<String, HealthCheck>

    @GET("agent/services")
    suspend fun getServices(@QueryMap query: Map<String, String>): Map<String, Service>

    @GET("agent/service/{serviceId}")
    suspend fun getService(
        @Path("serviceId") id: String,
        @QueryMap query: Map<String, String>
    ): FullService

    @GET("agent/members")
    suspend fun getMembers(@QueryMap query: Map<String, String>): List<Member>

    @PUT("agent/force-leave/{node}")
    suspend fun forceLeave(
        @Path("node") node: String,
        @QueryName optionsParameters: List<String>
    )

    @PUT("agent/check/{state}/{checkId}")
    suspend fun check(
        @Path("state") state: String,
        @Path("checkId") checkId: String,
        @QueryMap query: Map<String, String>
    )

    @PUT("agent/join/{address}")
    suspend fun join(
        @Path("address") address: String,
        @QueryMap query: Map<String, String>
    )

    @PUT("agent/service/maintenance/{serviceId}")
    suspend fun toggleMaintenanceMode(
        @Path("serviceId") serviceId: String,
        @QueryMap query: Map<String, String>
    )
}
