package ai.tech.core.data.crud.model

import kotlinx.serialization.Serializable

@Serializable
public data class Page<T : Any>(
    public val entities: List<T>,
    public val totalCount: Long,
)