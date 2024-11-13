package ai.tech.core.misc.consul.client.health

import ai.tech.core.misc.consul.client.health.model.HealthCheck
import ai.tech.core.misc.consul.client.health.model.ServiceHealth
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface HealthClient {

    @GET("health/node/{node}")
    fun getNodeChecks(
        @Path("node") node: String,
        @QueryMap query: Map<String, Any>,
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String>
    ): Call<List<HealthCheck>>

    @GET("health/checks/{service}")
    fun getServiceChecks(
        @Path("service") service: String,
        @QueryMap query: Map<String, Any>,
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String>
    ): Call<List<HealthCheck>>

    @GET("health/state/{state}")
    fun getChecksByState(
        @Path("state") state: String,
        @QueryMap query: Map<String, Any>,
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String>
    ): Call<List<HealthCheck>>

    @GET("health/service/{service}")
    fun getServiceInstances(
        @Path("service") service: String,
        @QueryMap query: Map<String, Any>,
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String>
    ): Call<List<ServiceHealth>>
}






