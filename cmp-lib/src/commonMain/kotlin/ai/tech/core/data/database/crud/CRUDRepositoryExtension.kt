package ai.tech.core.data.database.crud

import ai.tech.core.data.DataPagingSource
import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.crud.model.Order
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable
import app.cash.paging.COUNT_UNDEFINED
import app.cash.paging.MAX_SIZE_UNBOUNDED
import app.cash.paging.Pager
import app.cash.paging.PagingConfig

public fun <T : Any> CRUDRepository<T>.findPager(
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    limitOffset: LimitOffset,
    initialOffset: Long = 0,
    enablePlaceholders: Boolean = true,
    maxSize: Int = MAX_SIZE_UNBOUNDED,
    jumpThreshold: Int = COUNT_UNDEFINED,
): Pager<Long, T> = Pager(PagingConfig(limitOffset.limit.toInt(), limitOffset.offset.toInt(), enablePlaceholders, limitOffset.limit.toInt(), maxSize, jumpThreshold), null) {
    DataPagingSource({ find(sort, predicate, it) }, initialOffset)
}

public fun <T : Any> CRUDRepository<T>.findPager(
    projections: List<Variable>,
    sort: List<Order>? = null,
    predicate: BooleanVariable? = null,
    limitOffset: LimitOffset,
    initialOffset: Long = 0,
    enablePlaceholders: Boolean = true,
    maxSize: Int = MAX_SIZE_UNBOUNDED,
    jumpThreshold: Int = COUNT_UNDEFINED,
): Pager<Long, List<Any?>> = Pager(PagingConfig(limitOffset.limit.toInt(), limitOffset.offset.toInt(), enablePlaceholders, limitOffset.limit.toInt(), maxSize, jumpThreshold), null) {
    DataPagingSource({ find(projections, sort, predicate, it) }, initialOffset)
}

