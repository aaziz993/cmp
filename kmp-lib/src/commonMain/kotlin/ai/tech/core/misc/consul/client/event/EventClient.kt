package ai.tech.core.misc.consul.client.event

import ai.tech.core.misc.consul.client.event.model.Event
import ai.tech.core.misc.consul.model.parameter.EventParameters
import ai.tech.core.misc.consul.model.parameter.QueryParameters
import de.jensklingenberg.ktorfit.Ktorfit
import kotlinx.serialization.json.JsonPrimitive

/**
 * HTTP Client for /v1/event/ endpoints.
 *
 * @see [The Consul API Docs](http://www.consul.io/docs/agent/http.html.event)
 */
public class EventClient internal constructor(ktorfit: Ktorfit) {

    /**
     * Constructs an instance of this class.
     *
     * @param ktorfit The [Ktorfit] to build a client from.
     */
    private val api: EventApi = ktorfit.createEventApi()

    /**
     * Fires a Consul event.
     *
     * PUT /v1/event/fire/{name}
     *
     * @param name The name of the event.
     * @param eventParameters The event specific options to use.
     * @param payload Optional string payload.
     * @return The newly created [Event].
     */
    public suspend fun fire(name: String, eventParameters: EventParameters, payload: String): Event =
        api.fire(
            name,
            JsonPrimitive(payload),
            eventParameters.query,
        )

    /**
     * Fires a Consul event.
     *
     * PUT /v1/event/fire/{name}
     *
     * @param name The name of the event.
     * @param eventParameters The event specific options to use.
     * @return The newly created [Event].
     */
    public suspend fun fire(name: String, eventParameters: EventParameters): Event = api.fire(name, eventParameters.query)

    /**
     * Lists events for the Consul agent.
     *
     * GET /v1/event/listname={name}
     *
     * @param name Event name to filter.
     * @param queryParameters The query options to use.
     * @return A [List] object containing [Event] objects.
     */
    public suspend fun list(name: String? = null, queryParameters: QueryParameters = QueryParameters()): List<Event> =
        api.list(name?.let { mapOf("name" to name) }.orEmpty() + queryParameters.query)
}
