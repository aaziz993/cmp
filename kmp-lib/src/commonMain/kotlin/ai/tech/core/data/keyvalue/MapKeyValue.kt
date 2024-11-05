package ai.tech.core.data.keyvalue

import ai.tech.core.misc.type.TypeResolver
import ai.tech.core.misc.type.callOrNull
import ai.tech.core.misc.type.containsOrNull
import ai.tech.core.misc.type.decode
import ai.tech.core.misc.type.model.Entry
import ai.tech.core.misc.type.removeOrNull
import kotlinx.atomicfu.locks.ReentrantLock
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.serialization.json.Json

@Suppress("UNCHECKED_CAST")
public open class MapKeyValue(
    private val map: MutableMap<String, Any?> = mutableMapOf(),
) : AbstractKeyValue() {

    private val stateFlow = MutableStateFlow<Entry<List<String>, Any?>>(Entry(emptyList(), null))

    override suspend fun contains(keys: List<String>): Boolean = map.containsOrNull(keys)!!

    override suspend fun <T> set(keys: List<String>, value: T): Unit = lock.withLock {
        map.callOrNull(keys, value, false)
        stateFlow.update { Entry(keys, value) }
    }

    override suspend fun <T> get(
        keys: List<String>,
        type: TypeResolver,
        defaultValue: T?
    ): T = (map.callOrNull(keys)?.let { json.decode(it, type) } ?: defaultValue) as T

    override suspend fun <T> getFlow(
        keys: List<String>,
        type: TypeResolver,
    ): Flow<T> = stateFlow.filter { it.key == keys }.map { it.value } as Flow<T>

    public override suspend fun remove(keys: List<String>) {
        map.removeOrNull(keys)
    }

    override suspend fun clear(): Unit = map.clear()

    override suspend fun flush(): Unit = Unit

    override suspend fun size(): Int = map.size
}
