package ai.tech.core.misc.consul.client.status

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface StatusClient {

    @GET("status/leader")
    fun getLeader(@QueryMap options: Map<String, Any>): Call<String>

    @GET("status/peers")
    fun getPeers(@QueryMap options: Map<String, Any>): Call<List<String>>
}
