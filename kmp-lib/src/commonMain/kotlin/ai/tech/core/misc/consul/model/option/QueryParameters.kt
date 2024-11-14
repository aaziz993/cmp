package ai.tech.core.misc.consul.model.option

import ai.tech.core.misc.type.serializer.bignum.BigIntegerSerial
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class QueryParameters(
    val wait: String? = null,
    val token: String? = null,
    val hash: String? = null,
    val index: BigIntegerSerial? = null,
    val near: String? = null,
    val dc: String? = null,
    val filter: String? = null,
    val ns: String? = null,
    val wan: Boolean? = null,
    val segment: String? = null,
    val note: String? = null,
    val enable: Boolean? = null,
    val reason: String? = null,
    @Transient
    val nodeMeta: List<String>? = null,
    @Transient
    val tag: List<String>? = null,
    @Transient
    val consistencyMode: ConsistencyMode = ConsistencyMode.DEFAULT
) : Parameters {

    init {
        if (wait != null) {
            check(!(index == null && hash == null)) { "If wait is specified, index/hash must also be specified" }
            check(index == null || hash == null) { "Cannot specify index and hash ath the same time" }
        }
    }

    @Transient
    override val query: Map<String, String> = super.query + consistencyMode.param?.let { mapOf(it to "") }.orEmpty()

    @Transient
    override val headers: Map<String, String> = consistencyMode.additionalHeaders
}
