package ai.tech.core.misc.consul.model.option

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class TokenQueryOptions(
    val policy: String? = null,
    val role: String? = null,
    @SerialName("authmethod")
    val authMethod: String? = null,
    @SerialName("authmethod-ns")
    val authMethodNamespace: String? = null,
    val ns: String? = null,
) : ParamAdder() {

    public companion object {

        public val BLANK: TokenQueryOptions = TokenQueryOptions()
    }
}
