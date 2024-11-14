package ai.tech.core.misc.consul.client.status

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface StatusApi {

    @GET("status/leader")
    suspend fun getLeader(@QueryMap parameters: Map<String, String> = emptyMap()): String

    @GET("status/peers")
    suspend fun getPeers(@QueryMap parameters: Map<String, String> = emptyMap()): List<String>
}
