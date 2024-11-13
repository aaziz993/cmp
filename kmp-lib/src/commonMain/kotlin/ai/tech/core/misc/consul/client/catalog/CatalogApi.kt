package ai.tech.core.misc.consul.client.catalog

import ai.tech.core.misc.consul.client.catalog.model.CatalogDeregistration
import ai.tech.core.misc.consul.client.catalog.model.CatalogNode
import ai.tech.core.misc.consul.client.catalog.model.CatalogRegistration
import ai.tech.core.misc.consul.client.catalog.model.CatalogService
import ai.tech.core.misc.consul.client.health.model.Node
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.HeaderMap
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap

public interface CatalogApi {

    @GET("catalog/datacenters")
    public suspend fun getDatacenters(@HeaderMap headers: Map<String, String> = emptyMap()): List<String>

    @GET("catalog/nodes")
    public suspend fun getNodes(
        @QueryMap query: Map<String, String> = emptyMap(),
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): List<Node>

    @GET("catalog/node/{node}")
    public suspend fun getNode(
        @Path("node") node: String,
        @QueryMap query: Map<String, String> = emptyMap(),
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): CatalogNode

    @GET("catalog/services")
    public suspend fun getServices(
        @QueryMap query: Map<String, String> = emptyMap(),
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): Map<String, List<String>>

    @GET("catalog/service/{service}")
    public suspend fun getService(
        @Path("service") service: String,
        @QueryMap queryMeta: Map<String, String> = emptyMap(),
        @Query("tag") tag: List<String>,
        @Query("node-meta") nodeMeta: List<String>,
        @HeaderMap headers: Map<String, String> = emptyMap()
    ): List<CatalogService>

    @PUT("catalog/register")
    public suspend fun register(
        @Body registration: CatalogRegistration,
        @QueryMap options: Map<String, String> = emptyMap()
    )

    @PUT("catalog/deregister")
    public suspend fun deregister(
        @Body deregistration: CatalogDeregistration,
        @QueryMap options: Map<String, String> = emptyMap()
    )
}
