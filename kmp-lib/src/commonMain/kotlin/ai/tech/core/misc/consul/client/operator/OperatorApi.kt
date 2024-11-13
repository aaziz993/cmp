package ai.tech.core.misc.consul.client.operator

import ai.tech.core.misc.consul.client.operator.model.RaftConfiguration
import de.jensklingenberg.ktorfit.http.DELETE
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Query
import de.jensklingenberg.ktorfit.http.QueryMap

public interface OperatorApi {
    @GET("operator/raft/configuration")
    public suspend fun getConfiguration(@QueryMap query: Map<String, String>): RaftConfiguration

    @DELETE("operator/raft/peer")
    public suspend fun deletePeer(
        @Query("address") address: String,
        @QueryMap query: Map<String, String>
    )
}
