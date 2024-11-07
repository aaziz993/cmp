package ai.tech.core.misc.location.localization.model.config

import kotlin.collections.component1
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class Localization(
    val strings: Map<String, String> = emptyMap(),
    val arrayStrings: Map<String, List<String>> = emptyMap()
) {

    @Transient
    public val translations: Map<String, List<String>> = strings.mapValues { (_, v) -> listOf(v) } + arrayStrings
}
