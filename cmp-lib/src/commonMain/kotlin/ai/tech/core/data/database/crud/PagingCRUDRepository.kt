package ai.tech.core.data.database.crud

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.database.paging.DataPagingSource
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import app.cash.paging.COUNT_UNDEFINED
import app.cash.paging.MAX_SIZE_UNBOUNDED
import app.cash.paging.Pager
import app.cash.paging.PagingConfig

public class PagingCRUDRepository<T : Any>(
    repository: CRUDRepository<T>,
    public val initialOffset: Long = 0,
    public val enablePlaceholders: Boolean = true,
    public val maxSize: Int = MAX_SIZE_UNBOUNDED,
    public val jumpThreshold: Int = COUNT_UNDEFINED,
) : CRUDRepository<T> by repository {

    public fun findPager(
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
        limitOffset: LimitOffset
    ): Pager<Long, T> = Pager(PagingConfig(limitOffset.limit.toInt(), limitOffset.offset.toInt(), enablePlaceholders, limitOffset.limit.toInt(), maxSize, jumpThreshold), null) {
        DataPagingSource({ find(sort, predicate, it) }, initialOffset)
    }

    public fun findPager(
        projections: List<Variable>,
        sort: List<Order>? = null,
        predicate: BooleanVariable? = null,
        limitOffset: LimitOffset,
    ): Pager<Long, List<Any?>> = Pager(PagingConfig(limitOffset.limit.toInt(), limitOffset.offset.toInt(), enablePlaceholders, limitOffset.limit.toInt(), maxSize, jumpThreshold), null) {
        DataPagingSource({ find(projections, sort, predicate, it) }, initialOffset)
    }
}

