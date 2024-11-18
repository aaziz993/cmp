package ai.tech.core.data.paging.model

public interface RemoteKeys<T : Any> {

    public val newsId: Long
    public val prevKey: T?
    public val currentKey: T?
    public val nextKey: T?
}
