package ai.tech.core.misc.consul.client.coordinate

import ai.tech.core.misc.consul.client.coordinate.model.Coordinate
import ai.tech.core.misc.consul.client.coordinate.model.Datacenter
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface CoordinateClient {

    @GET("coordinate/datacenters")
    fun getDatacenters(): Call<List<Datacenter>>

    @GET("coordinate/nodes")
    fun getNodes(@QueryMap query: Map<String, String>): Call<List<Coordinate>>
}
