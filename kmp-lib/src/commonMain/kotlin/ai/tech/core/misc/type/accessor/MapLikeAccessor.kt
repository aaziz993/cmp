package ai.tech.core.misc.type.accessor

import ai.tech.core.misc.type.multiple.map
import ai.tech.core.misc.type.model.Entry
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Suppress("UNCHECKED_CAST")
public class MapLikeAccessor internal constructor(
    override val instance: Any,
    private val map: Map<*, *>,
    override val parentKey: Any? = null,
) : Accessor {

    override val keyType: KType = typeOf<Any?>()

    override fun valueType(key: Any?): KType = typeOf<Any?>()

    override fun iterator(): Iterator<Map.Entry<Any?, Any?>> =
        map.iterator().map { (k, v) -> Entry(k, v) }

    override fun contains(key: Any?): Boolean = map.contains(key)

    override fun get(key: Any?): Any? = map[key]

    override fun set(key: Any?, value: Any?) {
        require(instance is Map<*, *>) {
            error("Cannot set property of the type ${instance::class.simpleName}")
        }

        (map as MutableMap<Any?, Any?>)[key] = value
    }

    override fun remove(key: Any?): Any? {
        require(instance is Map<*, *>) {
            error("Cannot remove property of the type ${instance::class.simpleName}")
        }

        return (map as MutableMap).remove(key)
    }

    override fun clear(): Unit {
        require(instance is Map<*, *>) {
            error("Cannot clear properties of the type ${instance::class.simpleName}")
        }

        (map as MutableMap).clear()
    }
}
