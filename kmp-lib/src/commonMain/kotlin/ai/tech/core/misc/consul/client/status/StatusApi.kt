package ai.tech.core.misc.consul.client.status

import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.QueryMap

public interface StatusApi {

    @GET("status/leader")
    public suspend fun getLeader(@QueryMap options: Map<String, String>): String

    @GET("status/peers")
    public suspend fun getPeers(@QueryMap options: Map<String, String>): List<String>
}
