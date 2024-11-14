package ai.tech.core.misc.consul.model.option

import kotlinx.serialization.Serializable

@Serializable
public data class PutOptions(
    val cas: Long? = null,
    val acquire: String? = null,
    val release: String? = null,
    val dc: String? = null,
    val token: String? = null,
) : ParamAdder() {

    public companion object {

        public val BLANK: PutOptions = PutOptions()
    }
}
