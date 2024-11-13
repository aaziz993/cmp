package ai.tech.core.misc.consul.client.coordinate

import ai.tech.core.misc.consul.client.coordinate.model.Coordinate
import ai.tech.core.misc.consul.client.coordinate.model.Datacenter
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

public interface CoordinateApi {

    @GET("coordinate/datacenters")
    public suspend fun getDatacenters(): List<Datacenter>

    @GET("coordinate/nodes")
    public suspend fun getNodes(@QueryMap query: Map<String, String>): List<Coordinate>
}
