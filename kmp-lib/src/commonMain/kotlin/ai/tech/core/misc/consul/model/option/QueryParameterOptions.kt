package ai.tech.core.misc.consul.model.option

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class QueryParameterOptions(
    @SerialName("replace-existing-checks")
    val replaceExistingChecks: Boolean? = null,
    val prune: Boolean? = null
) : ParamAdder() {

    override val queryParameters: List<String>
        get() =
            listOfNotNull(
                replaceExistingChecks?.takeIf { it == true }?.let { "replace-existing-checks" },
                prune?.takeIf { it == true }?.let { "prune" },
            )

    public companion object {

        public val BLANK: QueryParameterOptions = QueryParameterOptions()
    }
}
