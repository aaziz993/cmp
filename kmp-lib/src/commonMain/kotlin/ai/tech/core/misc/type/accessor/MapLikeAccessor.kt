package ai.tech.core.misc.type.accessor

import ai.tech.core.misc.type.multiple.map
import ai.tech.core.misc.type.model.Entry
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Suppress("UNCHECKED_CAST")
public class MapLikeAccessor internal constructor(
    override val instance: Any,
    private val map: Map<*, *>,
    private val keyTransformer: (Any?) -> Any? = { it },
    override val parentKey: Any? = null,
) : Accessor {
    override val keyType: KType = typeOf<Any?>()

    override fun valueType(key: Any?): KType = typeOf<Any?>()

    override fun iterator(): Iterator<Map.Entry<Any?, Any?>> =
        map.iterator().map { (k, v) -> Entry(keyTransformer(k), v) }

    override fun contains(key: Any?): Boolean = map.keys.any { keyTransformer(it) == key }

    override fun call(
        key: Any?,
        arg: Any?,
        spread: Boolean,
    ): Any? = if (spread) {
        if (arg == null) {
            map.entries.find { (k, _) -> keyTransformer(k) == key }?.value
        } else when (arg) {
            is Map<*, *> -> (map as MutableMap<Any?, Any?>).putAll(arg)
            else -> IllegalArgumentException("Unknown argument \"$arg\"")
        }
    } else {
        (map as MutableMap<Any?, Any?>)[keyTransformer(key)] = arg
    }

    override fun remove(key: Any?): Any? {
        map.keys.forEach {
            if (keyTransformer(it) == key) {
                return (map as MutableMap).remove(it)
            }
        }
        return null
    }

    override fun clear(): Unit = (map as MutableMap).clear()
}