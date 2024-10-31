package ai.tech.core.misc.type.multiple

import ai.tech.core.misc.type.accessor
import ai.tech.core.misc.type.callOrNull

//////////////////////////////////////////////////////////MAP///////////////////////////////////////////////////////////
public fun <T> Map<T, *>.firstKey(value: Any?): T? = filterValues { it == value }.keys.firstOrNull()

public fun <K : V, V> Map<K, V>.valueOrKey(key: K): V = this[key] ?: key

public inline fun Map<String, *>.toDeepMap(
    keySplit: (String) -> List<String>,
): Map<String, Any?> =
    mutableMapOf<String, Any?>().apply {
        this@toDeepMap.forEach { (k, v) ->
            callOrNull(keySplit(k), v, false) { _, _, _ -> mutableMapOf<String, Any?>().accessor() }
        }
    }

