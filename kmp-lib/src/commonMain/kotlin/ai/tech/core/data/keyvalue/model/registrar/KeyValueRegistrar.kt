package ai.tech.core.data.keyvalue.model.registrar

import ai.tech.core.misc.type.TypeResolver
import ai.tech.core.data.keyvalue.KeyValues
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


public class KeyValueRegistrar<T>(
    private val key: String? = null,
    private val keys: List<String>? = null,
    private val type: TypeResolver,
    private val defaultValue: T? = null,
) : ReadOnlyProperty<KeyValues, T> {
    override fun getValue(thisRef: KeyValues, property: KProperty<*>): T =
        runBlocking {
            thisRef.get(
                (keys ?: thisRef.keys) + (key ?: property.name),
                type,
                defaultValue,
            )
        }
}
