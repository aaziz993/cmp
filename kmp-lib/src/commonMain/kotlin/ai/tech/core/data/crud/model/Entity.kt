package ai.tech.core.data.crud.model

public interface Entity<ID : Comparable<ID>> {

    public val id: ID?
}
