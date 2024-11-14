package ai.tech.core.misc.consul.client.session

import com.google.common.collect.ImmutableMap
import de.jensklingenberg.ktorfit.Ktorfit

/**
 * HTTP Client for /v1/session/ endpoints.
 *
 * @see [The Consul API Docs](http://www.consul.io/docs/agent/http.html.session)
 */
public class SessionClient internal constructor(ktorfit: Ktorfit){
    /**
     * Constructs an instance of this class.
     *
     * @param retrofit The [Retrofit] to build a client from.
     */
    private val api: SessionApi = ktorfit.createSessionApi()

    /**
     * Create Session.
     *
     * PUT /v1/session/create
     *
     * @param value The session to create.
     * @return ID of the newly created session .
     */
    public fun createSession(value: Session): SessionCreatedResponse {
        return createSession(value, null)
    }

    /**
     * Create Session.
     *
     * PUT /v1/session/create
     *
     * @param value The session to create.
     * @param dc    The data center.
     * @return Response containing the session ID.
     */
    public fun createSession(value: Session, dc: String): SessionCreatedResponse {
        return api.createSession(value, dcQuery(dc))
    }

    private fun dcQuery(dc: String): Map<String, String> {
        return if (dc != null) ImmutableMap.of("dc", dc) else Collections.emptyMap()
    }

    public fun renewSession(sessionId: String): Optional<SessionInfo> {
        return renewSession(null, sessionId)
    }

    /**
     * Renews a session.
     *
     * @param dc        The datacenter.
     * @param sessionId The session ID to renew.
     * @return The [SessionInfo] object for the renewed session.
     */
    public fun renewSession(dc: String, sessionId: String): Optional<SessionInfo> {
        val sessionInfo: List<SessionInfo> = http.extract(
            api.renewSession(
                sessionId,
                ImmutableMap.of(), dcQuery(dc)
            )
        )

        return if (sessionInfo == null || sessionInfo.isEmpty()) Optional.empty() else Optional.of(sessionInfo.get(0))
    }

    /**
     * Destroys a session.
     *
     * PUT /v1/session/destroy/{sessionId}
     *
     * @param sessionId The session ID to destroy.
     */
    public fun destroySession(sessionId: String) {
        destroySession(sessionId, null)
    }

    /**
     * Destroys a session.
     *
     * PUT /v1/session/destroy/{sessionId}
     *
     * @param sessionId The session ID to destroy.
     * @param dc        The data center.
     */
    public fun destroySession(sessionId: String, dc: String) {
        api.destroySession(sessionId, dcQuery(dc))
    }

    /**
     * Retrieves session info.
     *
     * GET /v1/session/info/{sessionId}
     *
     * @param sessionId
     * @return [SessionInfo].
     */
    public fun getSessionInfo(sessionId: String): Optional<SessionInfo> {
        return getSessionInfo(sessionId, null)
    }

    /**
     * Retrieves session info.
     *
     * GET /v1/session/info/{sessionId}
     *
     * @param sessionId
     * @param dc        Data center
     * @return [SessionInfo].
     */
    public fun getSessionInfo(sessionId: String, dc: String): Optional<SessionInfo> {
        val sessionInfo: List<SessionInfo> = api.getSessionInfo(sessionId, dcQuery(dc))

        return if (sessionInfo == null || sessionInfo.isEmpty()) Optional.empty() else Optional.of(sessionInfo.get(0))
    }

    /**
     * Lists all sessions.
     *
     * GET /v1/session/list
     *
     * @param dc The data center.
     * @return A list of available sessions.
     */
    public fun listSessions(dc: String): List<SessionInfo> {
        return api.listSessions(dcQuery(dc))
    }

    /**
     * Lists all sessions.
     *
     * GET /v1/session/list
     *
     * @return A list of available sessions.
     */
    public fun listSessions(): List<SessionInfo> {
        return listSessions(null)
    }
}
