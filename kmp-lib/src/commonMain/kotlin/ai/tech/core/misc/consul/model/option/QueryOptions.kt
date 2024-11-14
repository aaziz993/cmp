package ai.tech.core.misc.consul.model.option

import ai.tech.core.misc.type.serializer.bignum.BigIntegerSerial
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class QueryOptions(
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
    val nodeMeta: List<String>? = null,
    val tag: List<String>? = null,
    @Transient
    val consistencyMode: ConsistencyMode = ConsistencyMode.DEFAULT
) : ParamAdder() {

    init {
        if (wait != null) {
            check(!(index == null && hash == null)) { "If wait is specified, index/hash must also be specified" }
            check(index == null || hash == null) { "Cannot specify index and hash ath the same time" }
        }
    }

    override val query: Map<String, String>
        get() = super.query + consistencyMode.param?.let { mapOf(it to "") }.orEmpty()

    override val headers: Map<String, String>
        get() = consistencyMode.additionalHeaders

    public companion object {

        public val BLANK: QueryOptions = QueryOptions()
    }
}
