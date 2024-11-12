package ai.tech.core.data.database.crud.model

import kotlinx.serialization.Serializable

@Serializable
public data class LimitOffset(
    public val offset: Long = 0,
    public val limit: Long = 1,
)
