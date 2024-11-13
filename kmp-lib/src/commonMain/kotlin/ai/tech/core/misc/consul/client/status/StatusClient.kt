package ai.tech.core.misc.consul.client.status

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface StatusClient {

    @GET("status/leader")
    suspend fun getLeader(@QueryMap options: Map<String, String>): String

    @GET("status/peers")
    suspend fun getPeers(@QueryMap options: Map<String, String>): List<String>
}
