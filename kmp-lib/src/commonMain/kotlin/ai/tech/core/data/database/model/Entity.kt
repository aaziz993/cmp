package ai.tech.core.data.database.model

public interface Entity<ID : Comparable<ID>> {

    public val id: ID?
}
