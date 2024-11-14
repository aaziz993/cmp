package ai.tech.core.misc.type.multiple

import ai.tech.core.misc.type.accessor
import ai.tech.core.misc.type.set

//////////////////////////////////////////////////////////MAP///////////////////////////////////////////////////////////
public fun <T> Map<T, *>.firstKey(value: Any?): T? = filterValues { it == value }.keys.firstOrNull()

public fun <K : V, V> Map<K, V>.valueOrKey(key: K): V = this[key] ?: key

public fun Map<String, *>.toNestedKey(
    delimit: (String) -> List<String>,
): Map<String, Any?> =
    mutableMapOf<String, Any?>().apply {
        this@toNestedKey.forEach { (k, v) ->
            set(delimit(k), v) { _, _, _ -> mutableMapOf<String, Any?>().accessor() }
        }
    }

public fun Map<String, *>.toNestedKey(delimiter: String): Map<String, Any?> = toNestedKey { it.split(delimiter) }


