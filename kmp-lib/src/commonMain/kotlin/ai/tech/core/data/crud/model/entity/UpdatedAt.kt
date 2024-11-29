package ai.tech.core.data.crud.model.entity

public interface UpdatedAt<T : Comparable<T>> {

    public val updatedAt: T?
}
