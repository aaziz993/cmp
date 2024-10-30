package ai.tech.core.misc.type.accessor

import kotlin.reflect.KType

public interface Accessor : Iterable<Map.Entry<Any?, Any?>> {
    public val parentKey: Any?

    public val instance: Any

    public val keyType: KType

    public fun valueType(key: Any?): KType

    public fun contains(key: Any?): Boolean

    public fun call(
        key: Any?,
        arg: Any? = null,
        spread: Boolean = true,
    ): Any?

    public fun remove(key: Any?): Any?

    public fun clear()
}