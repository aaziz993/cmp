package ai.tech.core.data.crud.store5.model

public interface EntityFailedSync {

    public val predicate: String?

    // Timestamp of the last failed sync attempt for the given key.
    public val timestamp: Long

    public val sync: Sync
}
