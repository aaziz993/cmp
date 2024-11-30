package ai.tech.core.data.crud.client.model

public data class EntityRemoteKeysImpl<ID : Any>(
    override val entityId: ID,
    override val prevKey: Long?,
    override val currentKey: Long?,
    override val nextKey: Long?
) : EntityRemoteKeys<ID>
