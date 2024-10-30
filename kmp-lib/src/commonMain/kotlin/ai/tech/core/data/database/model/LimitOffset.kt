package ai.tech.core.data.database.model

import kotlinx.serialization.Serializable

@Serializable
public data class LimitOffset(
    public val offset: Long? = null,
    public val limit: Long? = null,
)
