package ai.tech.core.misc.consul.model.option

import kotlinx.serialization.Serializable

@Serializable
public data class RoleOptions(
    val policy: String? = null,
    val ns: String? = null
) : ParamAdder() {

    public companion object {

        public val BLANK: RoleOptions = RoleOptions()
    }
}
