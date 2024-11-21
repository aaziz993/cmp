package ai.tech.core.misc.consul.client.session

import ai.tech.core.misc.consul.client.AbstractConsulClient
import ai.tech.core.misc.consul.client.session.model.Session
import ai.tech.core.misc.consul.client.session.model.SessionCreatedResponse
import ai.tech.core.misc.consul.client.session.model.SessionInfo
import de.jensklingenberg.ktorfit.Ktorfit
import io.ktor.client.HttpClient

/**
 * HTTP Client for /v1/session/ endpoints.
 *
 * @see [The Consul API Docs](http://www.consul.io/docs/agent/http.html.session)
 */
public class SessionClient(
    httpClient: HttpClient,
    address: String,
    aclToken: String? = null
) : AbstractConsulClient(httpClient, address, aclToken) {

    /**
     * Constructs an instance of this class.
     *
     * @param ktorfit The [Ktorfit] to build a client from.
     */
    private val api: SessionApi = ktorfit.createSessionApi()

    /**
     * Create Session.
     *
     * PUT /v1/session/create
     *
     * @param value The session to create.
     * @param dc    The data center.
     * @return Response containing the session ID.
     */
    public suspend fun create(value: Session, dc: String? = null): SessionCreatedResponse =
        api.create(value, dc?.let { mapOf("dc" to dc) }.orEmpty())

    /**
     * Renews a session.
     *
     * @param dc        The datacenter.
     * @param sessionId The session ID to renew.
     * @return The [SessionInfo] object for the renewed session.
     */
    public suspend fun renew(sessionId: String, dc: String? = null): SessionInfo? =
        api.renew(sessionId, query = dc?.let { mapOf("dc" to dc) }.orEmpty()).singleOrNull()

    /**
     * Destroys a session.
     *
     * PUT /v1/session/destroy/{sessionId}
     *
     * @param sessionId The session ID to destroy.
     * @param dc        The data center.
     */
    public suspend fun destroy(sessionId: String, dc: String? = null): Unit =
        api.destroy(sessionId, dc?.let { mapOf("dc" to dc) }.orEmpty())

    /**
     * Retrieves session info.
     *
     * GET /v1/session/info/{sessionId}
     *
     * @param sessionId
     * @param dc        Data center
     * @return [SessionInfo].
     */
    public suspend fun getInfo(sessionId: String, dc: String? = null): SessionInfo? =
        api.getInfo(sessionId, dc?.let { mapOf("dc" to dc) }.orEmpty()).singleOrNull()

    /**
     * Lists all sessions.
     *
     * GET /v1/session/list
     *
     * @param dc The data center.
     * @return A list of available sessions.
     */
    public suspend fun list(dc: String? = null): List<SessionInfo> =
        api.list(dc?.let { mapOf("dc" to dc) }.orEmpty())
}
