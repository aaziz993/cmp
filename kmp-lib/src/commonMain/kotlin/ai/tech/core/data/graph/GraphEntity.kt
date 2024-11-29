package ai.tech.core.data.graph

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.crud.model.entity.Entity
import ai.tech.core.data.expression.f

public abstract class GraphEntity<T : GraphEntity<T, ID>, ID : Comparable<ID>>(
    override val id: ID? = null
): Entity<ID> {

    @Suppress("UNCHECKED_CAST")
    public suspend fun CRUDRepository<T>.save(): Unit = insert(listOf(this as T))

    public suspend fun CRUDRepository<T>.delete(): Boolean = delete("id".f eq id!!) > 0L

    override fun equals(other: Any?): Boolean =
        this === other || (other is GraphEntity<*, *> && this::class == other::class && id == other.id)

    override fun hashCode(): Int = id.hashCode()
}
