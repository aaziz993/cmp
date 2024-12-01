package ai.tech.core.misc.type.multiple

import ai.tech.core.misc.type.accessor
import ai.tech.core.misc.type.set

//////////////////////////////////////////////////////////MAP///////////////////////////////////////////////////////////
public fun <K, V> Map<K, V>.filterKeysIn(keys: List<K>): Map<K, V> = filterKeys(keys::contains)

public fun <K, V> Map<K, V>.filterKeysIn(vararg key: K): Map<K, V> = filterKeys(key::contains)

public fun <K, V> Map<K, V>.takeIfNotEmpty(): Map<K, V>? = takeIf(Map<K, V>::isNotEmpty)

public fun <K, V : List<*>> Map<K, V>.filterValuesNotEmpty(): Map<K, V> = filterValues(List<*>::isNotEmpty)

public fun <K, V : Map<*, *>> Map<K, V>.filterValuesNotEmpty(): Map<K, V> = filterValues(Map<*, *>::isNotEmpty)

public fun <K, V> Map<K, V>.filterValuesIsNotNull(): Map<K, V> = filterValues { it != null }

public fun <K : V, V> Map<K, V>.valueOrKey(key: K): V = this[key] ?: key

public fun Map<String, *>.toDeepMap(
    delimit: (String) -> List<String>,
): Map<String, Any?> =
    mutableMapOf<String, Any?>().apply {
        this@toDeepMap.forEach { (key, value) ->
            set(delimit(key), value) { _, _, value -> (value ?: mutableMapOf<String, Any?>()).accessor() }
        }
    }

public fun Map<String, *>.toDeepMap(delimiter: String): Map<String, Any?> = toDeepMap { key -> key.split(delimiter) }


