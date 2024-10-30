package ai.tech.core.misc.type.multiple

//////////////////////////////////////////////////////////MAP///////////////////////////////////////////////////////////
public fun <T> Map<T, *>.getFirstKey(value: Any?): T? = filterValues { it == value }.keys.firstOrNull()

public fun <K : V, V> Map<K, V>.getValueOrKey(key: K): V = this[key] ?: key

public inline fun Map<String, *>.toDeepMap(
    keySplitter: (String) -> List<String>,
): Map<String, Any?> =
    mutableMapOf<String, Any?>().apply {
        this@toDeepMap.forEach { (k, v) ->
            callOrNull(keySplitter(k), v, false) { _, _, _ -> mutableMapOf<String, Any?>().accessor() }
        }
    }

