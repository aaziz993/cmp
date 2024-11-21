package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.client.model.EntityProperty
import ai.tech.core.data.crud.client.model.MutationItem
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import app.cash.paging.ExperimentalPagingApi
import app.cash.paging.PagingConfig
import app.cash.paging.PagingSource
import app.cash.paging.RemoteMediator
import kotlinx.coroutines.CoroutineScope

@OptIn(ExperimentalPagingApi::class)
public class CRUDProjectionRefreshableMutablePager(
    private var projections: List<Variable>,
    private var sort: List<Order>? = null,
    private var predicate: BooleanVariable? = null,
    properties: List<EntityProperty>,
    private val create: () -> List<Any?>,
    config: PagingConfig,
    initialKey: Int? = null,
    remoteMediator: RemoteMediator<Int, List<Any?>>? = null,
    cacheCoroutineScope: CoroutineScope? = null,
    private val pagingSourceFactory: (projections: List<Variable>, sort: List<Order>?, predicate: BooleanVariable?) -> PagingSource<Int, List<Any?>>,
) : AbstractCRUDMutablePager<List<Any?>>(
    properties,
    List<Any?>::toList,
    config,
    initialKey,
    remoteMediator,
    cacheCoroutineScope,
) {

    override fun createPagingSource(): PagingSource<Int, List<Any?>> = pagingSourceFactory(projections, sort, predicate)

    public override fun refresh(): Unit = pagingSource.invalidate()

    override fun createEntity(): List<Any?> = create()

    override fun toEntity(item: MutationItem<List<Any?>>): List<Any?> = item.values.toList()

    public fun load(
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
