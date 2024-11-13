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
    fun getDatacenters(@HeaderMap headers: Map<String, String>): Call<List<String>>

    @GET("catalog/nodes")
    fun getNodes(
        @QueryMap query: Map<String, String>,
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String>
    ): Call<List<Node>>

    @GET("catalog/node/{node}")
    fun getNode(
        @Path("node") node: String,
        @QueryMap query: Map<String, String>,
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String>
    ): Call<CatalogNode>

    @GET("catalog/services")
    fun getServices(
        @QueryMap query: Map<String, String>,
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String>
    ): Call<Map<String, List<String>>>

    @GET("catalog/service/{service}")
    fun getService(
        @Path("service") service: String,
        @QueryMap queryMeta: Map<String, String>,
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String>
    ): Call<List<CatalogService>>

    @PUT("catalog/register")
    fun register(
        @Body registration: CatalogRegistration,
        @QueryMap options: Map<String, String>
    ): Call<Unit>

    @PUT("catalog/deregister")
    fun deregister(
        @Body deregistration: CatalogDeregistration,
        @QueryMap options: Map<String, String>
    ): Call<Unit>
}
