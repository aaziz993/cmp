package ai.tech.core.misc.consul.client.session

import ai.tech.core.misc.consul.client.session.model.Session
import ai.tech.core.misc.consul.client.session.model.SessionCreatedResponse
import ai.tech.core.misc.consul.client.session.model.SessionInfo
import de.jensklingenberg.ktorfit.Call
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap

internal interface SessionClient {

    @PUT("session/create")
    fun create(@Body value: Session, @QueryMap query: Map<String, String>): Call<SessionCreatedResponse>

    @PUT("session/renew/{sessionId}")
    fun renew(@Path("sessionId") sessionId: String, @Body body: Map<String, String>, @QueryMap query: Map<String, String>): Call<List<SessionInfo>>

    @PUT("session/destroy/{sessionId}")
    fun destroy(@Path("sessionId") sessionId: String, @QueryMap query: Map<String, String>): Call<Unit>

    @GET("session/info/{sessionId}")
    fun getInfo(@Path("sessionId") sessionId: String, @QueryMap query: Map<String, String>): Call<List<SessionInfo>>

    @GET("session/list")
    fun list(@QueryMap query: Map<String, String>): Call<List<SessionInfo>>
}

