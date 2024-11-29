package ai.tech.core.data.crud.client

import ai.tech.core.data.crud.model.query.LimitOffset
import ai.tech.core.data.paging.AbstractPagingSource

public class CRUDPagingSource<T : Any>(
    private val fetchData: suspend (limitOffset: LimitOffset) -> List<T>,
    public val firstItemOffset: Int = 0,
    disablePrepend: Boolean = false,
) : AbstractPagingSource<Long, T>(disablePrepend) {

    override suspend fun fetchData(loadKey: Long?, pageSize: Int): List<T> =
        fetchData(LimitOffset((loadKey ?: 0) * pageSize.toLong() + firstItemOffset, pageSize.toLong()))

    override fun getPrevKey(loadKey: Long): Long = loadKey.dec()

    override fun getNextKey(loadKey: Long?): Long? = loadKey?.inc() ?: 1
}
