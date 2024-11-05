package ai.tech.core.data.keyvalue.model.registrar

import ai.tech.core.data.keyvalue.KeyValues
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

public class KeyValueTransformRegistrar<T>(
    private val key: String? = null,
    private val keys: List<String>? = null,
    private val transform: (Any) -> T
) : ReadOnlyProperty<KeyValues, T> {
    override fun getValue(thisRef: KeyValues, property: KProperty<*>): T =
        runBlocking {
            transform(
                thisRef.get(
                    (keys ?: thisRef.keys) + (key ?: property.name),
                    TypeResolver(Any::class),
                )
            )
        }
}
