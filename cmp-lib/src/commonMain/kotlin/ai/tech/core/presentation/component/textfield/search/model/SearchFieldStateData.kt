package ai.tech.core.presentation.component.textfield.search.model

import kotlinx.serialization.Serializable

@Serializable
public data class SearchFieldStateData(
    val value: String = "",
    val caseMatch: Boolean = true,
    val wordMatch: Boolean = true,
    val regexMatch: Boolean = false,
    val compareMatch: Int = 0
)
