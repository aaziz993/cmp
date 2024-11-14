package ai.tech.core.misc.consul.client.kv

import ai.tech.core.misc.consul.client.kv.model.Operation
import ai.tech.core.misc.consul.client.kv.model.TxResponse
import ai.tech.core.misc.consul.client.kv.model.Value
import ai.tech.core.misc.consul.model.option.ConsistencyMode
import ai.tech.core.misc.consul.model.option.DeleteParameters
import ai.tech.core.misc.consul.model.option.PutParameters
import ai.tech.core.misc.consul.model.option.QueryParameters
import ai.tech.core.misc.consul.model.option.TransactionParameters
import de.jensklingenberg.ktorfit.Ktorfit
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement

/**
 * HTTP Client for /v1/kv/ endpoints.
 */
public class KVClient(ktorfit: Ktorfit) {

    /**
     * Constructs an instance of this class.
     *
     * @param ktorfit The [Ktorfit] to build a client from.
     */
    private val api: KVApi = ktorfit.createKVApi()

    /**
     * Retrieves a [Value] for a specific key
     * from the key/value store.
     *
     * GET /v1/kv/{key}
     *
     * @param key The key to retrieve.
     * @param queryParameters The query options.
     * @return An optional value
     */
    public suspend fun getValue(key: String, queryParameters: QueryParameters = QueryParameters()): List<Value> =
        api.getValue(key.trimStart('/'), queryParameters.query)

    /**
     * Puts a value into the key/value store.
     *
     * @param key The key to use as index.
     * @param putParameters PUT options (e.g. wait, acquire).
     * @return `true` if the value was successfully indexed.
     */
    public suspend fun putValue(key: String, flags: Long = 0, putParameters: PutParameters = PutParameters()): Boolean =
        api.putValue(
            key.trimStart('/'),
            putParameters.query + if (flags == 0L) {
                emptyMap()
            }
            else {
                mapOf("flags" to flags.toString())
            },
        )

    /**
     * Puts a value into the key/value store.
     *
     * @param key The key to use as index.
     * @param value The value to index.
     * @param putParameters PUT options (e.g. wait, acquire).
     * @return `true` if the value was successfully indexed.
     */
    public suspend fun putValue(key: String, value: JsonElement, flags: Long = 0, putParameters: PutParameters = PutParameters()): Boolean =
        api.putValue(
            key.trimStart('/'),
            value,
            putParameters.query + if (flags == 0L) {
                emptyMap()
            }
            else {
                mapOf("flags" to flags.toString())
            },
        )

    /**
     * Retrieves a list of matching keys for the given key.
     *
     * GET /v1/kv/{key}keys&separator={separator}
     *
     * @param key The key to retrieve.
     * @param separator The separator used to limit the prefix of keys returned.
     * @param queryParameters The query options.
     * @return A list of zero to many keys.
     */
    public suspend fun getKeys(key: String, separator: String? = null, queryParameters: QueryParameters = QueryParameters()): List<String> =
        api.getKeys(key.trimStart('/'), queryParameters.query + mapOf("keys" to "true") + separator?.let { mapOf("separator" to it) }.orEmpty())

    /**
     * Deletes a specified key.
     *
     * DELETE /v1/kv/{key}
     *
     * @param key The key to delete.
     * @param deleteParameters DeleteParameters options (e.g. recurse, cas)
     */
    public suspend fun deleteKey(key: String, deleteParameters: DeleteParameters): Unit =
        api.deleteValues(key.trimStart('/'), deleteParameters.query)

    /**
     * Aquire a lock for a given key.
     *
     * PUT /v1/kv/{key}acquire={session}
     *
     * @param key The key to acquire the lock.
     * @param session The session to acquire lock.
     * @param value key value (usually - application specific info about the lock requester)
     * @return true if the lock is acquired successfully, false otherwise.
     */
    public suspend fun acquireLock(key: String, value: String = "", session: String): Boolean =
        putValue(key, JsonPrimitive(value), putParameters = PutParameters(acquire = session))

    /**
     * Retrieves a session string for a specific key from the key/value store.
     *
     * GET /v1/kv/{key}
     *
     * @param key The key to retrieve.
     * @return An optional string
     */
    public suspend fun getSession(key: String): String? =
        getValue(key).singleOrNull()?.let(Value::session)

    /**
     * Releases the lock for a given service and session.
     *
     * GET /v1/kv/{key}release={sessionId}
     *
     * @param key identifying the service.
     * @param sessionId
     *
     * @return [Boolean].
     */
    public suspend fun releaseLock(key: String, sessionId: String): Boolean =
        putValue(key, putParameters = PutParameters(release = sessionId))

    /**
     * Performs a Consul transaction.
     *
     * PUT /v1/tx
     *
     * @param transactionParameters transaction options (e.g. dc, consistency).
     * @param operations A list of KV operations.
     * @return A [TxResponse] containing results and potential errors.
     */
    public suspend fun performTransaction(
        transactionParameters: TransactionParameters = TransactionParameters(consistencyMode = ConsistencyMode.DEFAULT),
        vararg operations: Operation
    ): TxResponse =
        api.performTransaction(Json.Default.encodeToJsonElement(operations), transactionParameters.query)
}
