package ai.tech.core.data.keyvalue

import ai.tech.core.misc.type.TypeResolver
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.uuid.Serializer

public abstract class AbstractKeyValue {

    protected val lock: ReentrantLock = reentrantLock()

    protected val json: Json = ai.tech.core.misc.type.Json {
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

    public abstract suspend fun <T> get(keys: List<String>, serializer: KSerializer<T>, defaultValue: T? = null): T

    public suspend fun <T> get(key: String, serializer: KSerializer<T>, defaultValue: T? = null): T = get(listOf(key), serializer, defaultValue)

    public abstract suspend fun <T> getFlow(keys: List<String>, serializer: KSerializer<T>): Flow<T>

    public suspend fun <T> getFlow(key: String, serializer: KSerializer<T>): Flow<T> = getFlow(listOf(key), serializer)

    public abstract suspend fun remove(keys: List<String>)

    public suspend fun remove(key: String): Unit = remove(listOf(key))

    public abstract suspend fun clear()

    public abstract suspend fun flush()

    public abstract suspend fun size(): Int
}

public suspend inline fun <reified T:Any> AbstractKeyValue.get(
    keys: List<String>,
    defaultValue: T? = null
): T = get(keys, T::class.ser, defaultValue)

public suspend inline fun <reified T> AbstractKeyValue.get(
    key: String,
    typeParameters: List<TypeResolver> = emptyList(),
    defaultValue: T? = null
): T = get(key, TypeResolver(T::class, * typeParameters.toTypedArray()), defaultValue)

public suspend inline fun <reified T> AbstractKeyValue.getFlow(
    keys: List<String>,
    vararg typeParameters: TypeResolver,
): Flow<T> = getFlow(keys, TypeResolver(T::class, * typeParameters))

public suspend inline fun <reified T> AbstractKeyValue.getFlow(
    key: String,
    vararg typeParameters: TypeResolver,
): Flow<T> = getFlow(key, TypeResolver(T::class, * typeParameters))
