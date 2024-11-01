package ai.tech.core.misc.location.weblate.model

import kotlinx.serialization.Serializable

@Serializable
public data class WeblateUnitsResponse(
    override val count: Int,
    override val next: String?,
    override val previous: String?,
    override val results: Set<WeblateUnit>
) : WeblateResponse<WeblateUnit>
