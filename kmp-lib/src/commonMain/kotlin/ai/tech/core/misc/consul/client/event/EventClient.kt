package ai.tech.core.misc.consul.client.event

import com.orbitz.consul.async.ConsulResponseCallback
import de.jensklingenberg.ktorfit.Ktorfit

/**
 * HTTP Client for /v1/event/ endpoints.
 *
 * @see [The Consul API Docs](http://www.consul.io/docs/agent/http.html.event)
 */
public class EventClient internal constructor(ktorfit: Ktorfit){
    /**
     * Constructs an instance of this class.
     *
     * @param retrofit The [Retrofit] to build a client from.
     */
    private val api: EventApi = ktorfit.createEventApi()

    /**
     * Fires a Consul event.
     *
     * PUT /v1/event/fire/{name}
     *
     * @param name The name of the event.
     * @param eventOptions The event specific options to use.
     * @param payload Optional string payload.
     * @return The newly created [com.orbitz.consul.model.event.Event].
     */
    public fun fireEvent(name: String, eventOptions: EventOptions, payload: String): Event {
        return http.extract(
            api.fireEvent(
                name,
                RequestBody.create(MediaType.parse("text/plain"), payload),
                eventOptions.toQuery()
            )
        )
    }

    /**
     * Fires a Consul event.
     *
     * PUT /v1/event/fire/{name}
     *
     * @param name The name of the event.
     * @return The newly created [com.orbitz.consul.model.event.Event].
     */
    public fun fireEvent(name: String): Event {
        return fireEvent(name, EventOptions.BLANK)
    }

    /**
     * Fires a Consul event.
     *
     * PUT /v1/event/fire/{name}
     *
     * @param name The name of the event.
     * @param eventOptions The event specific options to use.
     * @return The newly created [com.orbitz.consul.model.event.Event].
     */
    public fun fireEvent(name: String, eventOptions: EventOptions): Event {
        return api.fireEvent(name, eventOptions.toQuery())
    }

    /**
     * Fires a Consul event.
     *
     * PUT /v1/event/fire/{name}
     *
     * @param name The name of the event.
     * @param payload Optional string payload.
     * @return The newly created [com.orbitz.consul.model.event.Event].
     */
    public fun fireEvent(name: String, payload: String): Event {
        return fireEvent(name, EventOptions.BLANK, payload)
    }

    /**
     * Lists events for the Consul agent.
     *
     * GET /v1/event/listname={name}
     *
     * @param name Event name to filter.
     * @param queryOptions The query options to use.
     * @return A [com.orbitz.consul.model.ConsulResponse] object containing
     * a list of [com.orbitz.consul.model.event.Event] objects.
     */
    public fun listEvents(name: String, queryOptions: QueryOptions): EventResponse {
        val query: Map<String, Object> = queryOptions.toQuery()
        if (StringUtils.isNotEmpty(name)) {
            query.put("name", name)
        }

        val response: ConsulResponse<List<Event>> = http.extractConsulResponse(api.listEvents(query))
        return ImmutableEventResponse.of(response.getResponse(), response.getIndex())
    }

    /**
     * Lists events for the Consul agent.
     *
     * GET /v1/event/listname={name}
     *
     * @param name Event name to filter.
     * @return A [com.orbitz.consul.model.ConsulResponse] object containing
     * a list of [com.orbitz.consul.model.event.Event] objects.
     */
    public fun listEvents(name: String): EventResponse {
        return listEvents(name, QueryOptions.BLANK)
    }

    /**
     * Lists events for the Consul agent.
     *
     * GET /v1/event/list
     *
     * @param queryOptions The query options to use.
     * @return A [com.orbitz.consul.model.ConsulResponse] object containing
     * a list of [com.orbitz.consul.model.event.Event] objects.
     */
    public fun listEvents(queryOptions: QueryOptions): EventResponse {
        return listEvents(null, queryOptions)
    }

    /**
     * Lists events for the Consul agent.
     *
     * GET /v1/event/list
     *
     * @return A [com.orbitz.consul.model.ConsulResponse] object containing
     * a list of [com.orbitz.consul.model.event.Event] objects.
     */
    public fun listEvents(): EventResponse {
        return listEvents(null, QueryOptions.BLANK)
    }

    /**
     * Asynchronously lists events for the Consul agent.
     *
     * GET /v1/event/listname={name}
     *
     * @param name Event name to filter.
     * @param queryOptions The query options to use.
     * @param callback The callback to asynchronously process the result.
     */
    public fun listEvents(name: String, queryOptions: QueryOptions, callback: EventResponseCallback) {
        val query: Map<String, Object> = queryOptions.toQuery()
        if (StringUtils.isNotEmpty(name)) {
            query.put("name", name)
        }

        http.extractConsulResponse(api.listEvents(query), createConsulResponseCallbackWrapper(callback))
    }

    private fun createConsulResponseCallbackWrapper(callback: EventResponseCallback): ConsulResponseCallback<List<Event>> {
        return object : ConsulResponseCallback<List<Event>>() {
            @Override
            public fun onComplete(response: ConsulResponse<List<Event>>) {
                callback.onComplete(ImmutableEventResponse.of(response.getResponse(), response.getIndex()))
            }

            @Override
            public fun onFailure(throwable: Throwable) {
                callback.onFailure(throwable)
            }
        }
    }

    /**
     * Asynchronously lists events for the Consul agent.
     *
     * GET /v1/event/list
     *
     * @param queryOptions The query options to use.
     * @param callback The callback to asynchronously process the result.
     */
    public fun listEvents(queryOptions: QueryOptions, callback: EventResponseCallback) {
        listEvents(null, queryOptions, callback)
    }

    /**
     * Asynchronously lists events for the Consul agent.
     *
     * GET /v1/event/list
     *
     * @param callback The callback to asynchronously process the result.
     */
    public fun listEvents(callback: EventResponseCallback) {
        listEvents(null, QueryOptions.BLANK, callback)
    }
}
