package ai.tech.core.misc.type.multiple

import ai.tech.core.misc.type.accessor
import ai.tech.core.misc.type.set

//////////////////////////////////////////////////////////MAP///////////////////////////////////////////////////////////
public fun <K, V> Map<K, V>.filterKeys(keys: List<K>): Map<K, V> = filterKeys(keys::contains)

public fun <K, V> Map<K, V>.filterKeys(vararg key: K): Map<K, V> = filterKeys(key::contains)

public fun <K, V : List<*>> Map<K, V>.filterValuesNotEmpty(): Map<K, V> = filterValues(List<*>::isNotEmpty)

public fun <K, V : Map<*, *>> Map<K, V>.filterValuesNotEmpty(): Map<K, V> = filterValues(Map<*, *>::isNotEmpty)

public fun <K, V> Map<K, V>.filterValuesNotNull(): Map<K, V> = filterValues { it != null }

public fun <T> Map<T, *>.firstKey(value: Any?): T? = filterValues { it == value }.keys.firstOrNull()

public fun <K : V, V> Map<K, V>.valueOrKey(key: K): V = this[key] ?: key

public fun Map<String, *>.splitNestedKey(
    delimit: (String) -> List<String>,
): Map<String, Any?> =
    mutableMapOf<String, Any?>().apply {
        this@splitNestedKey.forEach { (k, v) ->
            set(delimit(k), v) { _, _, _ -> mutableMapOf<String, Any?>().accessor() }
        }
    }

public fun Map<String, *>.splitNestedKey(delimiter: String): Map<String, Any?> = splitNestedKey { it.split(delimiter) }


