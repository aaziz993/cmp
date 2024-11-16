package ai.tech.core.data.keyvalue

import ai.tech.core.misc.type.contains
import ai.tech.core.misc.type.serializer.decodeFromAny
import ai.tech.core.misc.type.get
import ai.tech.core.misc.type.model.Entry
import ai.tech.core.misc.type.remove
import ai.tech.core.misc.type.serializer.encodeToAny
import ai.tech.core.misc.type.set
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.Json

@Suppress("UNCHECKED_CAST")
public open class MapKeyValue(
    private val map: MutableMap<String, Any?> = mutableMapOf(),
) : AbstractKeyValue() {

    private val stateFlow = MutableStateFlow<Entry<List<String>, Any?>>(Entry(emptyList(), null))

    override suspend fun contains(keys: List<String>): Boolean = map.contains(keys)

    override suspend fun <T> set(keys: List<String>, value: T) {
        value?.let(json::encodeToAny).let { encodedValue ->
            map.set(keys, encodedValue)
            stateFlow.update { Entry(keys, encodedValue) }
        }
    }

    override suspend fun <T> get(
        keys: List<String>,
        deserializer: DeserializationStrategy<T>,
        defaultValue: T?
    ): T =
        (map.get(keys)?.let { json.decodeFromAny(deserializer, it) }
            ?: defaultValue) as T

    override suspend fun <T> getFlow(
        keys: List<String>,
        deserializer: DeserializationStrategy<T>,
    ): Flow<T> = stateFlow.filter { it.key == keys }.map { it.value as T }

    public override suspend fun remove(keys: List<String>) {
        map.remove(keys)
    }

    override suspend fun clear(): Unit = map.clear()

    override suspend fun flush(): Unit = Unit

    override suspend fun size(): Long = map.size.toLong()
}
