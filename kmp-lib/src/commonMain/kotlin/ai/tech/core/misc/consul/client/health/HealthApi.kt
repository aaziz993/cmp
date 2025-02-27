package ai.tech.core.misc.consul.client.health

import ai.tech.core.misc.consul.client.health.model.HealthCheck
import ai.tech.core.misc.consul.client.health.model.ServiceHealth
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface HealthApi {

    @GET("health/node/{node}")
    suspend fun getNodeChecks(
        @Path("node") node: String,
        @QueryMap query: Map<String, String> = emptyMap(),
        @Query("tag") tag: List<String> = emptyList(),
        @Query("node-meta") nodeMeta: List<String> = emptyList(),
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): List<HealthCheck>

    @GET("health/checks/{service}")
    suspend fun getServiceChecks(
        @Path("service") service: String,
        @QueryMap query: Map<String, String> = emptyMap(),
        @Query("tag") tag: List<String> = emptyList(),
        @Query("node-meta") nodeMeta: List<String> = emptyList(),
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): List<HealthCheck>

    @GET("health/state/{state}")
    suspend fun getChecksByState(
        @Path("state") state: String,
        @QueryMap query: Map<String, String> = emptyMap(),
        @Query("tag") tag: List<String> = emptyList(),
        @Query("node-meta") nodeMeta: List<String> = emptyList(),
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): List<HealthCheck>

    @GET("health/service/{service}")
    suspend fun getServiceInstances(
        @Path("service") service: String,
        @QueryMap query: Map<String, String> = emptyMap(),
        @Query("tag") tag: List<String> = emptyList(),
        @Query("node-meta") nodeMeta: List<String> = emptyList(),
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): List<ServiceHealth>
}






