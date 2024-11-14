package ai.tech.core.misc.consul.model.option

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class TransactionOptions(
    val dc: String? = null,
    @Transient
    val consistencyMode: ConsistencyMode = ConsistencyMode.DEFAULT
) : ParamAdder() {

    override val query: Map<String, String>
        get() = super.query + consistencyMode.param?.let { mapOf(it to "") }.orEmpty()

    override val headers: Map<String, String>
        get() = consistencyMode.additionalHeaders

    public companion object {

        public val BLANK: TransactionOptions = TransactionOptions()
    }
}
