package ai.tech.core.misc.consul.client.operator

import ai.tech.core.misc.consul.client.operator.model.RaftConfiguration
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface OperatorClient {
    @GET("operator/raft/configuration")
    fun getConfiguration(@QueryMap query: Map<String, String>): Call<RaftConfiguration>

    @DELETE("operator/raft/peer")
    fun deletePeer(
        @Query("address") address: String,
        @QueryMap query: Map<String, String>
    ): Call<Unit>
}
