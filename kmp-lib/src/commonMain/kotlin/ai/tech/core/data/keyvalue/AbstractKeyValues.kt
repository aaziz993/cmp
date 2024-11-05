package ai.tech.core.data.keyvalue

public abstract class AbstractKeyValues(
    public override val sources: List<AbstractKeyValue>,
    parent: KeyValues? = null,
    key: String? = null,
) : KeyValues {

    override val keys: List<String> = (parent?.keys ?: emptyList()) + (key?.let { listOf(key) } ?: emptyList())

    public constructor(parent: KeyValues, key: String) : this(
        parent.sources,
        parent,
        key,
    )
}
