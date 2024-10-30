package ai.tech.core.misc.type.accessor

import ai.tech.core.misc.type.multiple.mapIndexed
import ai.tech.core.misc.type.multiple.toList
import ai.tech.core.misc.type.model.Entry
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

    override fun call(
        key: Any?,
        arg: Any?,
        spread: Boolean,
    ): Any? =
        key.toIndex().let {
            if (spread) {
                if (arg == null) {
                    if (it in instance.indices) {
                        instance[it]
                    } else {
                        null
                    }
                } else when (arg) {
                    is Array<*> -> (instance as MutableList).addAll(arg)
                    is Iterable<*> -> (instance as MutableList).addAll(arg)
                    is Iterator<*> -> (instance as MutableList).addAll(arg.toList())
                    is Sequence<*> -> (instance as MutableList).addAll(arg.toList())
                    else -> IllegalArgumentException("Unknown argument \"${'$'}arg\"")
                }
            } else {
                if (it in instance.indices) {
                    (instance as MutableList)[it] = arg
                } else {
                    (instance as MutableList).add(arg)
                }
            }
        }

    override fun remove(key: Any?): Any? = (instance as MutableList).removeAt(key.toIndex())

    override fun clear(): Unit = (instance as MutableList).clear()
}

private fun Any?.toIndex(): Int = when (this) {
    is Int -> this
    is String -> toInt()
    else -> throw IllegalArgumentException("Unknown key \"$this\"")
}