package ai.tech.core.data.database.crud.model

import kotlinx.serialization.Serializable

@Serializable
public data class LimitOffset(
    public val offset: Long? = null,
    public val limit: Long? = null,
)
