package ai.tech.core.data.store5.model

public interface FailedSync<Key> {

    public val key: Key

    // Timestamp of the last failed sync attempt for the given key.
    public val timestamp: Long

    public val sync: Sync
}
