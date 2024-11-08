@file:OptIn(InternalSerializationApi::class)

package ai.tech.core.data.keyvalue

import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer

public abstract class AbstractKeyValue {

    protected val lock: ReentrantLock = reentrantLock()

    public val json: Json = ai.tech.core.misc.type.Json {
        encodeDefaults = false
        ignoreUnknownKeys = true
    }

    public open suspend fun <T> transactional(block: suspend AbstractKeyValue.() -> T): T = lock.withLock {
        block()
    }

    public abstract suspend fun contains(keys: List<String>): Boolean

    public suspend fun contains(key: String): Boolean = contains(listOf(key))

    public abstract suspend fun <T> set(keys: List<String>, value: T)

    public suspend fun <T> set(key: String, value: T): Unit = set(listOf(key), value)

    public abstract suspend fun <T> get(keys: List<String>, deserializer: DeserializationStrategy<T>, defaultValue: T? = null): T

    public suspend fun <T> get(key: String, deserializer: DeserializationStrategy<T>, defaultValue: T? = null): T = get(listOf(key), deserializer, defaultValue)

    public abstract suspend fun <T> getFlow(keys: List<String>, deserializer: DeserializationStrategy<T>): Flow<T>

    public suspend fun <T> getFlow(key: String, deserializer: DeserializationStrategy<T>): Flow<T> = getFlow(listOf(key), deserializer)

    public abstract suspend fun remove(keys: List<String>)

    public suspend fun remove(key: String): Unit = remove(listOf(key))

    public abstract suspend fun clear()

    public abstract suspend fun flush()

    public abstract suspend fun size(): Int
}

public suspend inline fun <reified T> AbstractKeyValue.get(keys: List<String>, defaultValue: T? = null): T =
    get(keys, json.serializersModule.serializer(), defaultValue)

public suspend inline fun <reified T> AbstractKeyValue.get(key: String, defaultValue: T? = null): T =
    get(key, json.serializersModule.serializer(), defaultValue)

public suspend inline fun <reified T> AbstractKeyValue.getFlow(keys: List<String>): Flow<T> =
    getFlow(keys, json.serializersModule.serializer())

public suspend inline fun <reified T> AbstractKeyValue.getFlow(key: String): Flow<T> =
    getFlow(key, json.serializersModule.serializer())
