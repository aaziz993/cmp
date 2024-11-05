package ai.tech.core.data.keyvalue

import ai.tech.core.misc.type.TypeResolver
import kotlinx.coroutines.flow.Flow

public interface KeyValues {

    public val sources: List<AbstractKeyValue>
    public val keys: List<String>

    @Suppress("UNCHECKED_CAST")
    public suspend fun <T> get(
        keys: List<String>,
        type: TypeResolver,
        defaultValue: T? = null,
    ): T = (sources.map {
        it.get<T>(keys, type)
    }.find { it.isSuccess }?.getOrNull() ?: defaultValue) as T

    public suspend fun <T> get(
        key: String,
        type: TypeResolver,
        defaultValue: T? = null,
    ): T = get(keys + key, type, defaultValue)

    public suspend fun <T> getFlow(
        keys: List<String>,
        type: TypeResolver,
    ): Flow<T> =
        sources.map {
            it.getFlow<T>(keys, type)
        }.find { it.isSuccess }!!.getOrThrow()

    public suspend fun <T> getFlow(key: String, type: TypeResolver): Flow<T> =
        getFlow(keys + key, type)
}
