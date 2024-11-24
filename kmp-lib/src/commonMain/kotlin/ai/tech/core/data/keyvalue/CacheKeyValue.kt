package ai.tech.core.data.keyvalue

import ai.tech.core.misc.type.model.Entry
import io.github.reactivecircus.cache4k.Cache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.encodeToString

@Suppress("UNCHECKED_CAST")
public open class CacheKeyValue(
    private val cache: Cache<String, String>,
    public val keyDelimiter: Char = '.',
    public val nullValue: String = "null",
) : AbstractKeyValue() {

    private val stateFlow = MutableStateFlow<Entry<List<String>, Any?>>(Entry(emptyList(), null))

    override suspend fun contains(keys: List<String>): Boolean = cache.asMap().contains(keys.toKey())

    override suspend fun <T> set(keys: List<String>, value: T) {
        cache.put(keys.toKey(), value?.let(json::encodeToString) ?: nullValue)
        stateFlow.update { Entry(keys, value) }
    }

    override suspend fun <T> get(
        keys: List<String>,
        deserializer: DeserializationStrategy<T>,
        defaultValue: T?
    ): T = (cache.get(keys.toKey())?.let {
        if (it == nullValue) {
            null
        }
        else {
            json.decodeFromString(deserializer, it)
        }
    } ?: defaultValue) as T

    override suspend fun <T> getFlow(
        keys: List<String>,
        deserializer: DeserializationStrategy<T>,
    ): Flow<T> = stateFlow.filter { it.key == keys }.map { it.value as T }

    public override suspend fun remove(keys: List<String>): Unit = cache.invalidate(keys.toKey())

    override suspend fun clear(): Unit = cache.invalidateAll()

    override suspend fun flush(): Unit = Unit

    override suspend fun size(): Long = cache.asMap().size.toLong()

    private fun List<String>.toKey() = reduce { acc, v -> "$acc$keyDelimiter$v" }
}
