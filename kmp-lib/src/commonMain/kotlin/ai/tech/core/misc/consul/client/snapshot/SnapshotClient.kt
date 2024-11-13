package ai.tech.core.misc.consul.client.snapshot

import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement

internal interface SnapshotClient {

    @Streaming
    @GET("snapshot")
    suspend fun generate(@QueryMap query: Map<String, Any>): HttpStatement

    @PUT("snapshot")
    @Headers("Content-Type: application/binary")
    suspend fun restore(
        @QueryMap query: Map<String, Any>,
        @Body requestBody: RequestBody
    ): Call<Unit>
}
