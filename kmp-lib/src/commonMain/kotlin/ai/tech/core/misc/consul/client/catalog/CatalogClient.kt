package ai.tech.core.misc.consul.client.catalog

import ai.tech.core.misc.consul.client.catalog.model.CatalogDeregistration
import ai.tech.core.misc.consul.client.catalog.model.CatalogNode
import ai.tech.core.misc.consul.client.catalog.model.CatalogRegistration
import ai.tech.core.misc.consul.client.catalog.model.CatalogService
import ai.tech.core.misc.consul.client.health.model.Node
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface CatalogClient {

    @GET("catalog/datacenters")
    suspend fun getDatacenters(@HeaderMap headers: Map<String, String>): List<String>

    @GET("catalog/nodes")
    suspend fun getNodes(
        @QueryMap query: Map<String, String>,
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String>
    ): List<Node>

    @GET("catalog/node/{node}")
    suspend fun getNode(
        @Path("node") node: String,
        @QueryMap query: Map<String, String>,
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String>
    ): CatalogNode

    @GET("catalog/services")
    suspend fun getServices(
        @QueryMap query: Map<String, String>,
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String>
    ): Map<String, List<String>>

    @GET("catalog/service/{service}")
    suspend fun getService(
        @Path("service") service: String,
        @QueryMap queryMeta: Map<String, String>,
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String>
    ): List<CatalogService>

    @PUT("catalog/register")
    suspend fun register(
        @Body registration: CatalogRegistration,
        @QueryMap options: Map<String, String>
    )

    @PUT("catalog/deregister")
    suspend fun deregister(
        @Body deregistration: CatalogDeregistration,
        @QueryMap options: Map<String, String>
    )
}
