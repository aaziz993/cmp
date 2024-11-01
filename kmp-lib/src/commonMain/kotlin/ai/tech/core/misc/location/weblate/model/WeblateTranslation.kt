package ai.tech.core.misc.location.weblate.model

import kotlinx.serialization.Serializable

@Serializable
public data class WeblateTranslation(
    val language: WeblateLanguage,
    val component: WeblateComponent,
)
