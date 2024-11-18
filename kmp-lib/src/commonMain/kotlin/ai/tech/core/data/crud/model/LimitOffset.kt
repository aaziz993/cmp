package ai.tech.core.data.crud.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class LimitOffset(
    public val offset: Long = 0,
    public val limit: Long = 10,
) {

    public fun getPage(firstItemOffset: Int = 0): Long = (offset - firstItemOffset) / limit
}
