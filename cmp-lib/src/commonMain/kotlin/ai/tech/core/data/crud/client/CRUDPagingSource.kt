package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.model.LimitOffset
import ai.tech.core.data.paging.AbstractDataPagingSource

public class CRUDPagingSource<T : Any>(
    private val fetchData: suspend (limitOffset: LimitOffset) -> List<T>,
    public val firstItemOffset: Int = 0,
) : AbstractDataPagingSource<Int, T>() {

    override suspend fun fetchData(loadKey: Int?, pageSize: Int): List<T> =
        fetchData(LimitOffset((loadKey ?: 0) * pageSize.toLong() + firstItemOffset, pageSize.toLong()))

    override fun getPrevKey(loadKey: Int?): Int? = loadKey?.takeIf { it > 0 }?.dec()

    override fun getNextKey(loadKey: Int?): Int? = (loadKey ?: 0).inc()
}
