package ai.tech.core.misc.consul.client.event

import ai.tech.core.misc.consul.client.event.model.Event
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap
import kotlinx.serialization.json.JsonElement

internal interface EventClient {

    @PUT("event/fire/{name}")
    suspend fun fire(
        @Path("name") name: String,
        @Body payload: JsonElement,
        @QueryMap query: Map<String, String>
    ): Event

    @PUT("event/fire/{name}")
    suspend fun fire(
        @Path("name") name: String,
        @QueryMap query: Map<String, String>
    ): Event

    @GET("event/list")
    suspend fun list(@QueryMap query: Map<String, String>): List<Event>
}
