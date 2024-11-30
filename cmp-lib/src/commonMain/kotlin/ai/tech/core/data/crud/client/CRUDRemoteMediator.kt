package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.client.model.EntityRemoteKeys
import ai.tech.core.data.crud.client.model.EntityRemoteKeysImpl
import ai.tech.core.data.crud.model.query.LimitOffset
import ai.tech.core.data.expression.f
import ai.tech.core.data.paging.AbstractRemoteMediator
import ai.tech.core.data.paging.model.RemoteKeys
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.toList

public class CRUDRemoteMediator<Value : Any, ID : Any>(
    private val remoteRepository: CRUDRepository<Value>,
    private val localRepository: CRUDRepository<Value>,
    private val keysRepository: CRUDRepository<EntityRemoteKeys<ID>>,
    private val getEntityId: (Value) -> ID,
    public val firstItemOffset: Int = 0,
    cacheTimeout: Int?,
) : AbstractRemoteMediator<Long, Value>(cacheTimeout) {

    override suspend fun fetchRemoteData(loadKey: Long?, pageSize: Int): List<Value> =
        remoteRepository.find(limitOffset = LimitOffset((loadKey ?: 0) * pageSize + firstItemOffset, pageSize.toLong()))
            .toList()

    override suspend fun refreshCache(items: List<Value>, loadKey: Long?, pageSize: Int): Boolean = localRepository.transactional {
        val endOfPaginationReached = items.size < pageSize

        localRepository.delete()
        localRepository.insert(items)

        keysRepository.delete()
        keysRepository.insert(items.createRemoteKeys(loadKey, endOfPaginationReached))

        endOfPaginationReached
    }

    override suspend fun cache(items: List<Value>, loadKey: Long?, pageSize: Int): Boolean =
        localRepository.transactional {
            val endOfPaginationReached = items.size < pageSize

            localRepository.insert(items)

            keysRepository.insert(items.createRemoteKeys(loadKey, endOfPaginationReached))

            endOfPaginationReached
        }

    override suspend fun getRemoteKeys(item: Value): RemoteKeys<Long>? =
        keysRepository.find(predicate = "entityId".f eq getEntityId(item).toString())
            .firstOrNull()

    private fun List<Value>.createRemoteKeys(loadKey: Long?, endOfPaginationReached: Boolean): List<EntityRemoteKeys<ID>> {
        val prevKey = loadKey?.takeIf { it > 0 }?.dec()

        val nextKey = if (endOfPaginationReached) {
            null
        }
        else {
            (loadKey ?: 0).inc()
        }

        return map { item -> EntityRemoteKeysImpl(getEntityId(item), prevKey, loadKey, nextKey) }
    }
}
