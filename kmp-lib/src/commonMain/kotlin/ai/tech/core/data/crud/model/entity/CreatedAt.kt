package ai.tech.core.data.crud.model.entity

public interface CreatedAt<T : Comparable<T>> {

    public val createdAt: T?
}
