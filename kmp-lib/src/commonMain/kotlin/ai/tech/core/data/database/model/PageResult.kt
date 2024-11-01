package ai.tech.core.data.database.model

import kotlinx.serialization.Serializable

@Serializable
public class PageResult<T : Any>(
    public val items: List<T>,
    public val totalCount: Int,
)
