package ai.tech.core.misc.consul.client.session

import ai.tech.core.misc.consul.client.session.model.Session
import ai.tech.core.misc.consul.client.session.model.SessionCreatedResponse
import ai.tech.core.misc.consul.client.session.model.SessionInfo
import de.jensklingenberg.ktorfit.http.Body
import de.jensklingenberg.ktorfit.http.GET
import de.jensklingenberg.ktorfit.http.PUT
import de.jensklingenberg.ktorfit.http.Path
import de.jensklingenberg.ktorfit.http.QueryMap

public interface SessionApi {

    @PUT("session/create")
    public suspend fun create(@Body value: Session, @QueryMap query: Map<String, String> = emptyMap()): SessionCreatedResponse

    @PUT("session/renew/{sessionId}")
    public suspend fun renew(@Path("sessionId") sessionId: String, @Body body: Map<String, String> = emptyMap(), @QueryMap query: Map<String, String> = emptyMap()): List<SessionInfo>

    @PUT("session/destroy/{sessionId}")
    public suspend fun destroy(@Path("sessionId") sessionId: String, @QueryMap query: Map<String, String> = emptyMap())

    @GET("session/info/{sessionId}")
    public suspend fun getInfo(@Path("sessionId") sessionId: String, @QueryMap query: Map<String, String> = emptyMap()): List<SessionInfo>

    @GET("session/list")
    public suspend fun list(@QueryMap query: Map<String, String> = emptyMap()): List<SessionInfo>
}

