package ai.tech.core.misc.type.accessor

import ai.tech.core.misc.type.model.Entry
import ai.tech.core.misc.type.multiple.mapIndexed
import kotlin.reflect.KType
import kotlin.reflect.typeOf

public class ListAccessor internal constructor(
    override val instance: List<Any?>,
    override val parentKey: Any? = null,
) : Accessor {

    override val keyType: KType = typeOf<Int>()

    override fun valueType(key: Any?): KType = typeOf<Any?>()

    override fun iterator(): Iterator<Map.Entry<Int, Any?>> = instance.iterator().mapIndexed(::Entry)

    override fun contains(key: Any?): Boolean = key.toIndex() in instance.indices

    override fun get(key: Any?): Any? = instance[key.toIndex()]

    override fun set(key: Any?, value: Any?) {
        (instance as MutableList).add(key.toIndex(), value)
    }

    override fun remove(key: Any?): Any? = (instance as MutableList).removeAt(key.toIndex())

    override fun clear(): Unit = (instance as MutableList).clear()
}

private fun Any?.toIndex(): Int = when (this) {
    is Int -> this
    is String -> toInt()
    else -> throw IllegalArgumentException("Unknown key \"$this\"")
}
