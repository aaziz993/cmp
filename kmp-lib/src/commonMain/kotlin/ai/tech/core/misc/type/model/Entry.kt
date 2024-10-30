package ai.tech.core.misc.type.model

public data class Entry<out K, out V>(
    override val key: K,
    override val value: V
) : Map.Entry<K, V>