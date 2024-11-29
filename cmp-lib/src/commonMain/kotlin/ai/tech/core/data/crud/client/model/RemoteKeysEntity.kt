package ai.tech.core.data.crud.client.model

import ai.tech.core.data.paging.model.RemoteKeys
import kotlinx.serialization.Serializable

@Serializable
public data class RemoteKeysEntity<ID : Any>(
    val entityId: ID,
    override val prevKey: Long?,
    override val currentKey: Long?,
    override val nextKey: Long?
) : RemoteKeys<Long>
