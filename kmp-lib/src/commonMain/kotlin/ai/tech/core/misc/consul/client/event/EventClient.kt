package ai.tech.core.misc.consul.client.event

import ai.tech.core.misc.consul.client.event.model.Event
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface EventClient {

    @PUT("event/fire/{name}")
    fun fire(
        @Path("name") name: String,
        @Body payload: RequestBody,
        @QueryMap query: Map<String, Any>
    ): Call<Event>

    @PUT("event/fire/{name}")
    fun fire(
        @Path("name") name: String,
        @QueryMap query: Map<String, Any>
    ): Call<Event>

    @GET("event/list")
    fun list(@QueryMap query: Map<String, Any>): Call<List<Event>>
}
