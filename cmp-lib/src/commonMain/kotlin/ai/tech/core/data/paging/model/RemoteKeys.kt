package ai.tech.core.data.paging.model

public interface RemoteKeys<Key : Any> {

    public val prevKey: Key?
    public val currentKey: Key?
    public val nextKey: Key?
}
