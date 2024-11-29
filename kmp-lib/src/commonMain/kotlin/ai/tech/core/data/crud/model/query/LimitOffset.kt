package ai.tech.core.data.crud.model.query

import kotlinx.serialization.Serializable

@Serializable
public data class LimitOffset(
    public val offset: Long? = null,
    public val limit: Long? = null,
) {

    public fun getPage(firstItemOffset: Int = 0): Long = (offset - firstItemOffset) / limit
}
