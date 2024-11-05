package ai.tech.core.data.keyvalue.model.registrar

import com.javiersc.kotlinx.coroutines.run.blocking.runBlocking
import ai.tech.core.data.keyvalue.KeyValues
import core.type.model.TypeResolver
import kotlinx.coroutines.flow.Flow
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public class KeyValueFlowRegistrar<T>(
    private val key: String? = null,
    private val keys: List<String>? = null,
    private val type: TypeResolver
) : ReadOnlyProperty<KeyValues, Flow<T>> {
    override fun getValue(thisRef: KeyValues, property: KProperty<*>): Flow<T> =
        runBlocking { thisRef.getFlow<T>((keys ?: thisRef.keys) + (key ?: property.name), type) }
}
