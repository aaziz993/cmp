package ai.tech.core.data.location.weblate.model

import kotlinx.serialization.Serializable

@Serializable
public data class WeblateTranslationsResponse(
    override val count: Int,
    override val next: String?,
    override val previous: String?,
    override val results: Set<WeblateTranslation>
) : WeblateResponse<WeblateTranslation>
