package ai.tech.core.misc.consul.client.kv

import ai.tech.core.misc.consul.client.keyvalue.createKVApi
import ai.tech.core.misc.consul.client.kv.KVApi
import com.fasterxml.jackson.core.JsonProcessingException
import de.jensklingenberg.ktorfit.Ktorfit

/**
 * HTTP Client for /v1/kv/ endpoints.
 */
public class KVClient(ktorfit: Ktorfit) {
    /**
     * Constructs an instance of this class.
     *
     * @param retrofit The [Retrofit] to build a client from.
     */
    private val api: KVApi = ktorfit.createKVApi()

        /**
         * Retrieves a [com.orbitz.consul.model.kv.Value] for a specific key
         * from the key/value store.
         *
         * GET /v1/kv/{key}
         *
         * @param key The key to retrieve.
         * @return An [Optional] containing the value or [Optional.empty]
         */
    public fun getValue(key: String): Optional<Value> {
        return getValue(key, QueryOptions.BLANK)
    }

    /**
     * Retrieves a [com.orbitz.consul.model.ConsulResponse] with the
     * [com.orbitz.consul.model.kv.Value] for a spefici key from the
     * key/value store
     * @param key The key to retrieve
     * @return An [Optional] containing the [ConsulResponse] or [Optional.empty]
     */
    public fun getConsulResponseWithValue(key: String): Optional<ConsulResponse<Value>> {
        return getConsulResponseWithValue(key, QueryOptions.BLANK)
    }

    /**
     * Retrieves a [com.orbitz.consul.model.kv.Value] for a specific key
     * from the key/value store.
     *
     * GET /v1/kv/{key}
     *
     * @param key The key to retrieve.
     * @param queryOptions The query options.
     * @return An [Optional] containing the value or [Optional.empty]
     */
    public fun getValue(key: String, queryOptions: QueryOptions): Optional<Value> {
        try {
            return getSingleValue(
                    http.extract(
                            api.getValue(
                                    trimLeadingSlash(key),
                                    queryOptions.toQuery(),
                            ),
                            com.orbitz.consul.KVClient.Companion.NOT_FOUND_404,
                    ),
            )
        }
        catch (ignored: ConsulException) {
            if (ignored.getCode() !== com.orbitz.consul.KVClient.Companion.NOT_FOUND_404) {
                throw ignored
            }
        }

        return Optional.empty()
    }

    /**
     * Returns a [<] for a specific key from the kv store.
     * Contains the consul response headers along with the configuration value.
     *
     * GET /v1/kv/{key}
     *
     * @param key The key to retrieve.
     * @param queryOptions The query options.
     * @return An [Optional] containing the ConsulResponse or [Optional.empty]
     */
    public fun getConsulResponseWithValue(key: String, queryOptions: QueryOptions): Optional<ConsulResponse<Value>> {
        try {
            val consulResponse: ConsulResponse<List<Value>> =
                http.extractConsulResponse(
                        api.getValue(trimLeadingSlash(key), queryOptions.toQuery()),
                        com.orbitz.consul.KVClient.Companion.NOT_FOUND_404,
                )
            val consulValue: Optional<Value> = getSingleValue(consulResponse.getResponse())
            if (consulValue.isPresent()) {
                val result: ConsulResponse<Value> =
                    ConsulResponse(
                            consulValue.get(), consulResponse.getLastContact(),
                            consulResponse.isKnownLeader(), consulResponse.getIndex(),
                            consulResponse.getCacheReponseInfo(),
                    )
                return Optional.of(result)
            }
        }
        catch (ignored: ConsulException) {
            if (ignored.getCode() !== com.orbitz.consul.KVClient.Companion.NOT_FOUND_404) {
                throw ignored
            }
        }

        return Optional.empty()
    }

    /**
     * Asynchronously retrieves a [com.orbitz.consul.model.kv.Value] for a specific key
     * from the key/value store.
     *
     * GET /v1/kv/{key}
     *
     * @param key The key to retrieve.
     * @param queryOptions The query options.
     * @param callback Callback implemented by callee to handle results.
     */
    public fun getValue(key: String, queryOptions: QueryOptions, callback: ConsulResponseCallback<Optional<Value>>) {
        val wrapper: ConsulResponseCallback<List<Value>> = object : ConsulResponseCallback<List<Value>>() {
            @Override
            public fun onComplete(consulResponse: ConsulResponse<List<Value>>) {
                callback.onComplete(
                        ConsulResponse(
                                getSingleValue(consulResponse.getResponse()),
                                consulResponse.getLastContact(),
                                consulResponse.isKnownLeader(), consulResponse.getIndex(),
                                consulResponse.getCacheReponseInfo(),
                        ),
                )
            }

            @Override
            public fun onFailure(throwable: Throwable) {
                callback.onFailure(throwable)
            }
        }

        http.extractConsulResponse(
                api.getValue(trimLeadingSlash(key), queryOptions.toQuery()),
                wrapper,
                com.orbitz.consul.KVClient.Companion.NOT_FOUND_404,
        )
    }

    private fun getSingleValue(values: List<Value>): Optional<Value> {
        return if (values != null && values.size() !== 0) Optional.of(values.get(0)) else Optional.empty()
    }

    /**
     * Retrieves a list of [com.orbitz.consul.model.kv.Value] objects for a specific key
     * from the key/value store.
     *
     * GET /v1/kv/{key}recurse
     *
     * @param key The key to retrieve.
     * @return A list of zero to many [com.orbitz.consul.model.kv.Value] objects.
     */
    public fun getValues(key: String): List<Value> {
        return getValues(key, QueryOptions.BLANK)
    }

    /**
     * Retrieves a [ConsulResponse] with a list of [Value] objects along with
     * consul response headers for a specific key from the key/value store.
     *
     * GET /v1/kv/{key}recurse
     *
     * @param key The key to retrieve.
     * @return A [ConsulResponse] with a list of zero to many [Value] objects and
     * consul response headers.
     */
    public fun getConsulResponseWithValues(key: String): ConsulResponse<List<Value>> {
        return getConsulResponseWithValues(key, QueryOptions.BLANK)
    }

    /**
     * Retrieves a list of [com.orbitz.consul.model.kv.Value] objects for a specific key
     * from the key/value store.
     *
     * GET /v1/kv/{key}recurse
     *
     * @param key The key to retrieve.
     * @param queryOptions The query options.
     * @return A list of zero to many [com.orbitz.consul.model.kv.Value] objects.
     */
    public fun getValues(key: String, queryOptions: QueryOptions): List<Value> {
        val query: Map<String, Object> = queryOptions.toQuery()

        query.put("recurse", "true")

        val result: List<Value> = http.extract(
                api.getValue(trimLeadingSlash(key), query),
                com.orbitz.consul.KVClient.Companion.NOT_FOUND_404,
        )

        return if (result == null) Collections.emptyList() else result
    }

    /**
     * Retrieves a [ConsulResponse] with a list of [Value] objects along with
     * consul response headers for a specific key from the key/value store.
     *
     * GET /v1/kv/{key}recurse
     *
     * @param key The key to retrieve.
     * @param queryOptions The query options to use.
     * @return A [ConsulResponse] with a list of zero to many [Value] objects and
     * consul response headers.
     */
    public fun getConsulResponseWithValues(key: String, queryOptions: QueryOptions): ConsulResponse<List<Value>> {
        val query: Map<String, Object> = queryOptions.toQuery()

        query.put("recurse", "true")

        return http.extractConsulResponse(
                api.getValue(trimLeadingSlash(key), query),
                com.orbitz.consul.KVClient.Companion.NOT_FOUND_404,
        )
    }

    /**
     * Asynchronously retrieves a list of [com.orbitz.consul.model.kv.Value] objects for a specific key
     * from the key/value store.
     *
     * GET /v1/kv/{key}recurse
     *
     * @param key The key to retrieve.
     * @param queryOptions The query options.
     * @param callback Callback implemented by callee to handle results.
     */
    public fun getValues(key: String, queryOptions: QueryOptions, callback: ConsulResponseCallback<List<Value>>) {
        val query: Map<String, Object> = queryOptions.toQuery()

        query.put("recurse", "true")

        http.extractConsulResponse(
                api.getValue(trimLeadingSlash(key), query),
                callback,
                com.orbitz.consul.KVClient.Companion.NOT_FOUND_404,
        )
    }

    /**
     * Retrieves a string value for a specific key from the key/value store.
     *
     * GET /v1/kv/{key}
     *
     * @param key The key to retrieve.
     * @return An [Optional] containing the value as a string or
     * [Optional.empty]
     */
    public fun getValueAsString(key: String): Optional<String> {
        return getValueAsString(key, Charset.defaultCharset())
    }

    /**
     * Retrieves a string value for a specific key from the key/value store.
     *
     * GET /v1/kv/{key}
     *
     * @param key The key to retrieve.
     * @param charset The charset of the value
     * @return An [Optional] containing the value as a string or
     * [Optional.empty]
     */
    public fun getValueAsString(key: String, charset: Charset): Optional<String> {
        return getValue(key).flatMap({ v -> v.getValueAsString(charset) })
    }

    /**
     * Retrieves a list of string values for a specific key from the key/value
     * store.
     *
     * GET /v1/kv/{key}recurse
     *
     * @param key The key to retrieve.
     * @return A list of zero to many string values.
     */
    public fun getValuesAsString(key: String): List<String> {
        return getValuesAsString(key, Charset.defaultCharset())
    }

    /**
     * Retrieves a list of string values for a specific key from the key/value
     * store.
     *
     * GET /v1/kv/{key}recurse
     *
     * @param key The key to retrieve.
     * @param charset The charset of the value
     * @return A list of zero to many string values.
     */
    public fun getValuesAsString(key: String, charset: Charset): List<String> {
        val result: List<String> = ArrayList()

        for (value in getValues(key)!!) {
            value.getValueAsString(charset).ifPresent(result::add)
        }

        return result
    }

    /**
     * Puts a null value into the key/value store.
     *
     * @param key The key to use as index.
     * @return `true` if the value was successfully indexed.
     */
    public fun putValue(key: String): Boolean {
        return putValue(key, null, 0L, PutOptions.BLANK, Charset.defaultCharset())
    }

    /**
     * Puts a value into the key/value store.
     *
     * @param key The key to use as index.
     * @param value The value to index.
     * @return `true` if the value was successfully indexed.
     */
    public fun putValue(key: String, value: String): Boolean {
        return putValue(key, value, 0L, PutOptions.BLANK)
    }

    /**
     * Puts a value into the key/value store.
     *
     * @param key The key to use as index.
     * @param value The value to index.
     * @return `true` if the value was successfully indexed.
     */
    public fun putValue(key: String, value: String, charset: Charset): Boolean {
        return putValue(key, value, 0L, PutOptions.BLANK, charset)
    }

    /**
     * Puts a value into the key/value store.
     *
     * @param key The key to use as index.
     * @param value The value to index.
     * @param flags The flags for this key.
     * @return `true` if the value was successfully indexed.
     */
    public fun putValue(key: String, value: String, flags: Long): Boolean {
        return putValue(key, value, flags, PutOptions.BLANK)
    }

    /**
     * Puts a value into the key/value store.
     *
     * @param key The key to use as index.
     * @param value The value to index.
     * @param flags The flags for this key.
     * @return `true` if the value was successfully indexed.
     */
    public fun putValue(key: String, value: String, flags: Long, charset: Charset): Boolean {
        return putValue(key, value, flags, PutOptions.BLANK, charset)
    }

    /**
     * Puts a value into the key/value store.
     *
     * @param key The key to use as index.
     * @param value The value to index.
     * @param putOptions PUT options (e.g. wait, acquire).
     * @return `true` if the value was successfully indexed.
     */
    public fun putValue(key: String, value: String, flags: Long, putOptions: PutOptions): Boolean {
        return putValue(key, value, flags, putOptions, Charset.defaultCharset())
    }

    /**
     * Puts a value into the key/value store.
     *
     * @param key The key to use as index.
     * @param value The value to index.
     * @param putOptions PUT options (e.g. wait, acquire).
     * @return `true` if the value was successfully indexed.
     */
    public fun putValue(key: String, value: String, flags: Long, putOptions: PutOptions, charset: Charset): Boolean {
        checkArgument(StringUtils.isNotEmpty(key), "Key must be defined")
        val query: Map<String, Object> = putOptions.toQuery()

        if (flags != 0L) {
            query.put("flags", UnsignedLongs.toString(flags))
        }

        if (value == null) {
            return http.extract(
                    api.putValue(
                            trimLeadingSlash(key),
                            query,
                    ),
            )
        }
        else {
            return http.extract(
                    api.putValue(
                            trimLeadingSlash(key),
                            RequestBody.create(MediaType.parse("text/plain; charset=" + charset.name()), value), query,
                    ),
            )
        }
    }

    /**
     * Puts a value into the key/value store.
     *
     * @param key The key to use as index.
     * @param value The value to index.
     * @param putOptions PUT options (e.g. wait, acquire).
     * @return `true` if the value was successfully indexed.
     */
    public fun putValue(key: String, value: ByteArray, flags: Long, putOptions: PutOptions): Boolean {
        checkArgument(StringUtils.isNotEmpty(key), "Key must be defined")
        val query: Map<String, Object> = putOptions.toQuery()

        if (flags != 0L) {
            query.put("flags", UnsignedLongs.toString(flags))
        }

        if (value == null) {
            return http.extract(
                    api.putValue(
                            trimLeadingSlash(key),
                            query,
                    ),
            )
        }
        else {
            return http.extract(
                    api.putValue(
                            trimLeadingSlash(key),
                            RequestBody.create(MediaType.parse("application/octet-stream"), value), query,
                    ),
            )
        }
    }

    /**
     * Retrieves a list of matching keys for the given key.
     *
     * GET /v1/kv/{key}keys
     *
     * @param key The key to retrieve.
     * @return A list of zero to many keys.
     */
    public fun getKeys(key: String): List<String> {
        return getKeys(key, QueryOptions.BLANK)
    }

    /**
     * Retrieves a list of matching keys for the given key.
     *
     * GET /v1/kv/{key}keys
     *
     * @param key The key to retrieve.
     * @param queryOptions The query options.
     * @return A list of zero to many keys.
     */
    public fun getKeys(key: String, queryOptions: QueryOptions): List<String> {
        return getKeys(key, null, queryOptions)
    }

    /**
     * Retrieves a list of matching keys for the given key, limiting the prefix of keys
     * returned, only up to the given separator.
     *
     * GET /v1/kv/{key}keys&separator={separator}
     *
     * @param key The key to retrieve.
     * @param separator The separator used to limit the prefix of keys returned.
     * @return A list of zero to many keys.
     */
    public fun getKeys(key: String, separator: String): List<String> {
        return getKeys(key, separator, QueryOptions.BLANK)
    }

    /**
     * Retrieves a list of matching keys for the given key.
     *
     * GET /v1/kv/{key}keys&separator={separator}
     *
     * @param key The key to retrieve.
     * @param separator The separator used to limit the prefix of keys returned.
     * @param queryOptions The query options.
     * @return A list of zero to many keys.
     */
    public fun getKeys(key: String, separator: String, queryOptions: QueryOptions): List<String> {
        val query: Map<String, Object> = queryOptions.toQuery()
        query.put("keys", "true")
        if (separator != null) {
            query.put("separator", separator)
        }

        val result: List<String> = http.extract(
                api.getKeys(trimLeadingSlash(key), query),
                com.orbitz.consul.KVClient.Companion.NOT_FOUND_404,
        )
        return if (result == null) Collections.emptyList() else result
    }

    /**
     * Deletes a specified key.
     *
     * DELETE /v1/kv/{key}
     *
     * @param key The key to delete.
     */
    public fun deleteKey(key: String) {
        deleteKey(key, DeleteOptions.BLANK)
    }

    /**
     * Deletes a specified key and any below it.
     *
     * DELETE /v1/kv/{key}recurse
     *
     * @param key The key to delete.
     */
    public fun deleteKeys(key: String) {
        deleteKey(key, DeleteOptions.RECURSE)
    }

    /**
     * Deletes a specified key.
     *
     * DELETE /v1/kv/{key}
     *
     * @param key The key to delete.
     * @param deleteOptions DELETE options (e.g. recurse, cas)
     */
    public fun deleteKey(key: String, deleteOptions: DeleteOptions) {
        checkArgument(StringUtils.isNotEmpty(key), "Key must be defined")
        val query: Map<String, Object> = deleteOptions.toQuery()

        api.deleteValues(trimLeadingSlash(key), query)
    }

    /**
     * Aquire a lock for a given key.
     *
     * PUT /v1/kv/{key}acquire={session}
     *
     * @param key The key to acquire the lock.
     * @param session The session to acquire lock.
     * @return true if the lock is acquired successfully, false otherwise.
     */
    public fun acquireLock(key: String, session: String): Boolean {
        return acquireLock(key, "", session)
    }

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
    public fun acquireLock(key: String, value: String, session: String): Boolean {
        return putValue(key, value, 0, ImmutablePutOptions.builder().acquire(session).build())
    }

    /**
     * Retrieves a session string for a specific key from the key/value store.
     *
     * GET /v1/kv/{key}
     *
     * @param key The key to retrieve.
     * @return An [Optional] containing the value as a string or
     * [Optional.empty]
     */
    public fun getSession(key: String): Optional<String> {
        return getValue(key).flatMap(Value::getSession)
    }

    /**
     * Releases the lock for a given service and session.
     *
     * GET /v1/kv/{key}release={sessionId}
     *
     * @param key identifying the service.
     * @param sessionId
     *
     * @return [SessionInfo].
     */
    public fun releaseLock(key: String, sessionId: String): Boolean {
        return putValue(key, "", 0, ImmutablePutOptions.builder().release(sessionId).build())
    }

    /**
     * Performs a Consul transaction.
     *
     * PUT /v1/tx
     *
     * @param operations A list of KV operations.
     * @return A [ConsulResponse] containing results and potential errors.
     */
    public fun performTransaction(vararg operations: Operation): ConsulResponse<TxResponse> {
        val immutableTransactionOptions: ImmutableTransactionOptions =
            ImmutableTransactionOptions.builder().consistencyMode(ConsistencyMode.DEFAULT).build()
        return performTransaction(immutableTransactionOptions, operations)
    }

    /**
     * Performs a Consul transaction.
     *
     * PUT /v1/tx
     *
     * @param consistency The consistency to use for the transaction.
     * @param operations A list of KV operations.
     * @return A [ConsulResponse] containing results and potential errors.
     */
    @Deprecated
    @Deprecated(
            """Replaced by {@link #performTransaction(TransactionOptions, Operation...)}

      """,
    )
    public fun performTransaction(
        consistency: ConsistencyMode,
        vararg operations: Operation
    ): ConsulResponse<TxResponse> {
        val query: Map<String, Object> = if (consistency === ConsistencyMode.DEFAULT)
            ImmutableMap.of()
        else
            ImmutableMap.of(consistency.toParam().get(), "true")

        try {
            return http.extractConsulResponse(
                    api.performTransaction(
                            RequestBody.create(
                                    MediaType.parse("application/json"),
                                    Jackson.MAPPER.writeValueAsString(com.orbitz.consul.KVClient.Companion.kv(*operations)),
                            ),
                            query,
                    ),
            )
        }
        catch (e: JsonProcessingException) {
            throw ConsulException(e)
        }
    }

    /**
     * Performs a Consul transaction.
     *
     * PUT /v1/tx
     *
     * @param transactionOptions transaction options (e.g. dc, consistency).
     * @param operations A list of KV operations.
     * @return A [ConsulResponse] containing results and potential errors.
     */
    public fun performTransaction(
        transactionOptions: TransactionOptions,
        vararg operations: Operation
    ): ConsulResponse<TxResponse> {
        val query: Map<String, Object> = transactionOptions.toQuery()

        try {
            return http.extractConsulResponse(
                    api.performTransaction(
                            RequestBody.create(
                                    MediaType.parse("application/json"),
                                    Jackson.MAPPER.writeValueAsString(com.orbitz.consul.KVClient.Companion.kv(*operations)),
                            ),
                            query,
                    ),
            )
        }
        catch (e: JsonProcessingException) {
            throw ConsulException(e)
        }
    }
}
