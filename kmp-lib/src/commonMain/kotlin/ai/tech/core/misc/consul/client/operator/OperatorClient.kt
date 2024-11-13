package ai.tech.core.misc.consul.client.operator

import ai.tech.core.misc.consul.client.operator.model.RaftConfiguration
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface OperatorClient {
    @GET("operator/raft/configuration")
    suspend fun getConfiguration(@QueryMap query: Map<String, String>): RaftConfiguration

    @DELETE("operator/raft/peer")
    suspend fun deletePeer(
        @Query("address") address: String,
        @QueryMap query: Map<String, String>
    )
}
