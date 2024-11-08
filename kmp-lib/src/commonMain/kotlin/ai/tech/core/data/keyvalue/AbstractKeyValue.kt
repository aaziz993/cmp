package ai.tech.core.data.keyvalue

import ai.tech.core.misc.type.TypeResolver
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.json.Json

public abstract class AbstractKeyValue {

    protected val lock: ReentrantLock = reentrantLock()

    protected val json: Json = Json {
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

    public abstract suspend fun <T> get(keys: List<String>, type: TypeResolver, defaultValue: T? = null): T

    public suspend fun <T> get(key: String, type: TypeResolver, defaultValue: T? = null): T = get(listOf(key), type, defaultValue)

    public abstract suspend fun <T> getFlow(keys: List<String>, type: TypeResolver): Flow<T>

    public suspend fun <T> getFlow(key: String, type: TypeResolver): Flow<T> = getFlow(listOf(key), type)

    public abstract suspend fun remove(keys: List<String>): Unit

    public suspend fun remove(key: String): Unit = remove(listOf(key))

    public abstract suspend fun clear()

    public abstract suspend fun flush()

    public abstract suspend fun size(): Int
}

public suspend inline fun <reified T> AbstractKeyValue.get(
    keys: List<String>,
    typeParameters: List<TypeResolver> = emptyList(),
    defaultValue: T? = null
): T = get(keys, TypeResolver(T::class, * typeParameters.toTypedArray()), defaultValue)

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
