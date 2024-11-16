package ai.tech.core.data.paging

import ai.tech.core.data.crud.model.LimitOffset
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.LoadType
import app.cash.paging.PagingState
import app.cash.paging.RemoteMediator
import app.cash.paging.RemoteMediatorInitializeAction
import app.cash.paging.RemoteMediatorMediatorResult
import app.cash.paging.RemoteMediatorMediatorResultError
import app.cash.paging.RemoteMediatorMediatorResultSuccess
import kotlinx.datetime.Clock

public interface Key<T : Any> {

    public val currentKey: T
}

@OptIn(ExperimentalPagingApi::class)
public abstract class AbstractDataRemoteMediator<Value : Any>(
    public val firstItemOffset: Int = 0,
    public val cacheTimeout: Int? = null,
) : RemoteMediator<Int, Value>() {

    protected abstract val cache: Cache<Value>

    override suspend fun initialize(): RemoteMediatorInitializeAction = if (cacheTimeout == null ||
        Clock.System.now().toEpochMilliseconds() - (getCacheCreationTime() ?: 0) < cacheTimeout) {
        RemoteMediatorInitializeAction.SKIP_INITIAL_REFRESH
    }
    else {
        RemoteMediatorInitializeAction.LAUNCH_INITIAL_REFRESH
    }

    @Suppress("UNCHECKED_CAST")
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, Value>
    ): RemoteMediatorMediatorResult {
        return try {
            // The network load method takes an optional String
            // parameter. For every page after the first, pass the String
            // token returned from the previous page to let it continue
            // from where it left off. For REFRESH, pass null to load the
            // first page.
            val loadKey = when (loadType) {
                LoadType.REFRESH -> {
                    //New Query so clear the DB
                    val key = getRemoteKeyClosestToCurrentPosition(state)
                    key?.currentKey ?: 0
                }

                // In this example, you never need to prepend, since REFRESH
                // will always load the first page in the list. Immediately
                // return, reporting end of pagination.
                LoadType.PREPEND -> {
                    val key = getRemoteKeyForFirstItem(state)
                    // If remoteKeys is null, that means the refresh result is not in the database yet.
                    val prevKey = key?.currentKey?.dec()
                    prevKey
                        ?: return RemoteMediatorMediatorResultSuccess(endOfPaginationReached = key != null) as RemoteMediatorMediatorResult
                }

                LoadType.APPEND -> {
                    val key = getRemoteKeyForLastItem(state)

                    // If remoteKeys is null, that means the refresh result is not in the database yet.
                    // We can return Success with endOfPaginationReached = false because Paging
                    // will call this method again if RemoteKeys becomes non-null.
                    // If remoteKeys is NOT NULL but its nextKey is null, that means we've reached
                    // the end of pagination for append.
                    val nextKey = key?.currentKey?.inc()
                    nextKey
                        ?: return RemoteMediatorMediatorResultSuccess(endOfPaginationReached = key != null) as RemoteMediatorMediatorResult
                }

                else -> 0
            }

            val limit = state.config.pageSize.toLong()

            val offset = loadKey * limit + firstItemOffset

            val data = fetchData(LimitOffset(offset, limit))

            if (loadType == LoadType.REFRESH) {
                //New query so we can delete everything.
                refreshCache(loadKey, data)
            }
            else {
                cache(loadKey, data)
            }

            RemoteMediatorMediatorResultSuccess(
                endOfPaginationReached = data.isEmpty(),
            )
        }
        catch (e: Exception) {
            RemoteMediatorMediatorResultError(e)
        } as RemoteMediatorMediatorResult
    }

    public abstract suspend fun fetchData(limitOffset: LimitOffset): List<Value>

    public abstract suspend fun refreshCache(loadKey: Int, items: List<Value>)

    public abstract suspend fun cache(loadKey: Int, items: List<Value>)

    public abstract suspend fun getItemKey(item: Value): Key<Int>?

    public open suspend fun getCacheCreationTime(): Long? = null

    /** LoadType.REFRESH
     * Gets called when it's the first time we're loading data, or when PagingDataAdapter.refresh() is called;
     * so now the point of reference for loading our data is the state.anchorPosition.
     * If this is the first load, then the anchorPosition is null.
     * When PagingDataAdapter.refresh() is called, the anchorPosition is the first visible position in the displayed list, so we will need to load the page that contains that specific item.
     */
    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, Value>): Key<Int>? {
        // The paging library is trying to load data after the anchor position
        // Get the item closest to the anchor position
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.let { getItemKey(it) }
        }
    }

    /** LoadType.Prepend
     * When we need to load data at the beginning of the currently loaded data set, the load parameter is LoadType.PREPEND
     */
    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, Value>): Key<Int>? {
        // Get the first page that was retrieved, that contained items.
        // From that first page, get the first item
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { getItemKey(it) }
    }

    /** LoadType.Append
     * When we need to load data at the end of the currently loaded data set, the load parameter is LoadType.APPEND
     */
    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, Value>): Key<Int>? {
        // Get the last page that was retrieved, that contained items.
        // From that last page, get the last item
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { getItemKey(it) }
    }
}
