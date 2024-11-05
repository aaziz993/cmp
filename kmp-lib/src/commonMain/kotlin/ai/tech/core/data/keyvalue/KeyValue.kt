package ai.tech.core.data.keyvalue

import ai.tech.core.misc.type.TypeResolver
import kotlinx.coroutines.flow.Flow

public interface KeyValue {

    public suspend fun <T> transactional(block: suspend KeyValue.() -> T): T

    public suspend fun contains(keys: List<String>): Boolean

    public suspend fun contains(key: String): Boolean = contains(listOf(key))

    public suspend fun <T> set(keys: List<String>, value: T)

    public suspend fun <T> set(key: String, value: T): Unit = set(listOf(key), value)

    public suspend fun <T> get(keys: List<String>, type: TypeResolver, defaultValue: T? = null): T

    public suspend fun <T> get(key: String, type: TypeResolver, defaultValue: T? = null): T = get(listOf(key), type, defaultValue)

    public suspend fun <T> getFlow(keys: List<String>, type: TypeResolver): Flow<T>

    public suspend fun <T> getFlow(key: String, type: TypeResolver): Flow<T> = getFlow(listOf(key), type)

    public suspend fun remove(keys: List<String>)

    public suspend fun remove(key: String): Unit = remove(listOf(key))

    public suspend fun clear()

    public suspend fun flush()
}

public suspend inline fun <reified T> KeyValue.get(
    keys: List<String>,
    typeParameters: List<TypeResolver> = emptyList(),
    defaultValue: T? = null
): T? = get(keys, TypeResolver(T::class, * typeParameters.toTypedArray()), defaultValue)

public suspend inline fun <reified T> KeyValue.get(
    key: String,
    typeParameters: List<TypeResolver> = emptyList(),
    defaultValue: T? = null
): T? = get(key, TypeResolver(T::class, * typeParameters.toTypedArray()), defaultValue)

public suspend inline fun <reified T> KeyValue.getFlow(
    keys: List<String>,
    vararg typeParameters: TypeResolver,
): Flow<T> = getFlow(keys, TypeResolver(T::class, * typeParameters))

public suspend inline fun <reified T> KeyValue.getFlow(
    key: String,
    vararg typeParameters: TypeResolver,
): Flow<T> = getFlow(key, TypeResolver(T::class, * typeParameters))
