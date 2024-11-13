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
    fun register(
        @Body registration: Registration,
        @QueryMap options: Map<String, String>,
        @QueryName optionsParameters: List<String>
    ): Call<Unit>

    @PUT("agent/service/deregister/{serviceId}")
    fun deregister(
        @Path("serviceId") serviceId: String,
        @QueryMap options: Map<String, String>
    ): Call<Unit>

    @PUT("agent/check/register")
    fun registerCheck(@Body check: Check): Call<Unit>

    @PUT("agent/check/deregister/{checkId}")
    fun deregisterCheck(@Path("checkId") checkId: String): Call<Unit>

    @GET("status/leader")
    fun ping(): Call<Unit>

    @GET("agent/self")
    fun getSelf(): Call<Agent>

    @GET("agent/checks")
    fun getChecks(@QueryMap optionsParameters: Map<String, String>): Call<Map<String, HealthCheck>>

    @GET("agent/services")
    fun getServices(@QueryMap query: Map<String, String>): Call<Map<String, Service>>

    @GET("agent/service/{serviceId}")
    fun getService(
        @Path("serviceId") id: String,
        @QueryMap query: Map<String, String>
    ): Call<FullService>

    @GET("agent/members")
    fun getMembers(@QueryMap query: Map<String, String>): Call<List<Member>>

    @PUT("agent/force-leave/{node}")
    fun forceLeave(
        @Path("node") node: String,
        @QueryName optionsParameters: List<String>
    ): Call<Unit>

    @PUT("agent/check/{state}/{checkId}")
    fun check(
        @Path("state") state: String,
        @Path("checkId") checkId: String,
        @QueryMap query: Map<String, String>
    ): Call<Unit>

    @PUT("agent/join/{address}")
    fun join(
        @Path("address") address: String,
        @QueryMap query: Map<String, String>
    ): Call<Unit>

    @PUT("agent/service/maintenance/{serviceId}")
    fun toggleMaintenanceMode(
        @Path("serviceId") serviceId: String,
        @QueryMap query: Map<String, String>
    ): Call<Unit>
}
