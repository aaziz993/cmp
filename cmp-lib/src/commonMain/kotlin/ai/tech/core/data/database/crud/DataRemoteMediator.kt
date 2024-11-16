package ai.tech.core.data.database.crud

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
import kotlinx.io.IOException

public interface Cache<T : Any> {

    public suspend fun transaction(block: suspend Cache<T>.() -> Unit)

    public suspend fun insert(entities: List<T>)

    public suspend fun delete()
}

@OptIn(ExperimentalPagingApi::class)
public class DataRemoteMediator<Value : Any>(
    private val fetchData: (LimitOffset) -> List<Value>,
    private val getNextPage: (Value) -> Int,
    private val cache: Cache<Value>,
    public val firstItemOffset: Int = 0,
    public val cacheTimeout: Int? = null,
) : RemoteMediator<Int, Value>() {

    override suspend fun initialize(): RemoteMediatorInitializeAction {

        return if (cacheTimeout == null || Clock.System.now().toEpochMilliseconds() - (moviesDatabase.getRemoteKeysDao().getCreationTime()
                ?: 0) < cacheTimeout) {
            RemoteMediatorInitializeAction.SKIP_INITIAL_REFRESH
        }
        else {
            RemoteMediatorInitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

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
            val page = when (loadType) {
                LoadType.REFRESH -> 0

                // In this example, you never need to prepend, since REFRESH
                // will always load the first page in the list. Immediately
                // return, reporting end of pagination.
                LoadType.PREPEND -> return RemoteMediatorMediatorResultSuccess(
                    endOfPaginationReached = true,
                ) as RemoteMediatorMediatorResult

                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()

                    lastItem?.let { getNextPage(it) } ?: return RemoteMediatorMediatorResultSuccess(
                        endOfPaginationReached = true,
                    ) as RemoteMediatorMediatorResult
                }

                else -> 0
            }

            val limit = state.config.pageSize.toLong()

            val data = fetchData(LimitOffset(page * limit + firstItemOffset, limit))

            cache.transaction {
                if (loadType == LoadType.REFRESH) {
                    delete()
                }

                insert(data)
            }

            RemoteMediatorMediatorResultSuccess(
                endOfPaginationReached = data.isEmpty(),
            )
        }
        catch (e: Exception) {
            RemoteMediatorMediatorResultError(e)
        } as RemoteMediatorMediatorResult
    }
}
