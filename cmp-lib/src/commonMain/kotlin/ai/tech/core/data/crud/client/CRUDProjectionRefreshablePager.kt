package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import ai.tech.core.data.paging.AbstractRefreshablePager
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.RemoteMediator
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalPagingApi::class)
public class CRUDProjectionRefreshablePager(
    private var projections: List<Variable>,
    private var sort: List<Order>? = null,
    private var predicate: BooleanVariable? = null,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, List<Any?>>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
    private val pagingSourceFactory: (projections: List<Variable>, sort: List<Order>?, predicate: BooleanVariable?) -> PagingSource<Int, List<Any?>>
) : AbstractRefreshablePager<Int, List<Any?>>(
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) {

    override fun createPagingSource(): PagingSource<Int, List<Any?>> = pagingSourceFactory(projections, sort, predicate)

    public fun refresh(
        projections: List<Variable>,
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
    ) {
        this.projections = projections
        this.sort = sort
        this.predicate = predicate
        refresh()
    }
}
