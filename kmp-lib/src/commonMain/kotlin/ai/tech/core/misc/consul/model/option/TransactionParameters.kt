package ai.tech.core.misc.consul.model.option

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class TransactionParameters(
    val dc: String? = null,
    @Transient
    val consistencyMode: ConsistencyMode = ConsistencyMode.DEFAULT
) : Parameters {

    @Transient
    override val query: Map<String, String> = super.query + consistencyMode.param?.let { mapOf(it to "") }.orEmpty()

    @Transient
    override val headers: Map<String, String> = consistencyMode.additionalHeaders
}
