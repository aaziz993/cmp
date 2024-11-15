package ai.tech.core.misc.location.localization.weblate.client.model

import kotlinx.serialization.Serializable

@Serializable
public data class WeblateTranslation(
    val language: WeblateLanguage,
    val component: WeblateComponent,
)
