package ai.tech.core.misc.consul.client.coordinate

import ai.tech.core.misc.consul.client.coordinate.model.Coordinate
import ai.tech.core.misc.consul.client.coordinate.model.Datacenter
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface CoordinateClient {

    @GET("coordinate/datacenters")
    suspend fun getDatacenters(): List<Datacenter>

    @GET("coordinate/nodes")
    suspend fun getNodes(@QueryMap query: Map<String, String>): List<Coordinate>
}
