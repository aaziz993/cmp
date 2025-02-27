package ai.tech.core.misc.consul.client.snapshot

import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.Headers
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.QueryMap
import de.jensklingenberg.ktorfit.http.Streaming
import io.ktor.client.statement.HttpStatement
import kotlinx.serialization.json.JsonElement

internal interface SnapshotApi {

    @Streaming
    @GET("snapshot")
    suspend fun generate(@QueryMap query: Map<String, String> = emptyMap()): HttpStatement

    @PUT("snapshot")
    @Headers("Content-Type: application/binary")
    suspend fun restore(
        @QueryMap query: Map<String, String> = emptyMap(),
        @Body requestBody: ByteArray
    )
}
