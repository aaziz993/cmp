package ai.tech.core.data.database.crud

import ai.tech.core.data.DataPagingSource
import ai.tech.core.data.database.crud.model.LimitOffset
import ai.tech.core.data.database.crud.model.Order
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import app.cash.paging.COUNT_UNDEFINED
import app.cash.paging.MAX_SIZE_UNBOUNDED
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.createPagingConfig

public class CRUDPresenter<T : Any>(
    public val repository: CRUDRepository<T>,
    public val enablePlaceholders: Boolean = true,
    public val maxSize: Int = MAX_SIZE_UNBOUNDED,
    public val jumpThreshold: Int = COUNT_UNDEFINED,
    public val initialOffset: Long) {

    public fun find(
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
        limitOffset: LimitOffset
    ): Pager<Long, T> = Pager(PagingConfig(limitOffset.limit.toInt(), limitOffset.offset.toInt(), enablePlaceholders, limitOffset.limit.toInt(), maxSize, jumpThreshold), null) {
        DataPagingSource({ repository.find(sort, predicate, it) }, initialOffset)
    }

    public fun find(
        projections: List<Variable>,
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
        limitOffset: LimitOffset
    ): Pager<Long, List<Any?>> = Pager(PagingConfig(limitOffset.limit.toInt(), limitOffset.offset.toInt(), enablePlaceholders, limitOffset.limit.toInt(), maxSize, jumpThreshold), null) {
        DataPagingSource({ repository.find(projections, sort, predicate, it) }, initialOffset)
    }
}

