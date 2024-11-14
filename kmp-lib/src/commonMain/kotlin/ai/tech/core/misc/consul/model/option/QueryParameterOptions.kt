package ai.tech.core.misc.consul.model.option

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class QueryParameterOptions(
    @SerialName("replace-existing-checks")
    val replaceExistingChecks: Boolean? = null,
    val prune: Boolean? = null
) : ParamAdder {

    @Transient
    override val queryParameters: List<String> = listOfNotNull(
        replaceExistingChecks?.takeIf { it == true }?.let { "replace-existing-checks" },
        prune?.takeIf { it == true }?.let { "prune" },
    )
}
