package ai.tech.core.misc.type.accessor

import kotlin.reflect.KType

public interface Accessor : Iterable<Map.Entry<Any?, Any?>> {

    public val parentKey: Any?

    public val instance: Any

    public val keyType: KType

    public fun valueType(key: Any?): KType

    public fun contains(key: Any?): Boolean

    public operator fun get(key: Any?): Any?

    public operator fun set(key: Any?, value: Any?)

    public fun remove(key: Any?): Any?

    public fun clear()
}
