package ai.tech.core.data.database.graph

import ai.tech.core.data.database.CRUDRepository
import ai.tech.core.data.expression.f

public abstract class GraphObject<T : GraphObject<T, ID>, ID : Comparable<ID>>(
    public val id: ID? = null
) {

    @Suppress("UNCHECKED_CAST")
    public suspend fun CRUDRepository<T>.save(): Unit = insert(listOf(this as T))

    public suspend fun CRUDRepository<T>.delete(): Boolean = delete("id".f eq id!!) > 0L

    override fun equals(other: Any?): Boolean =
        this === other || (other is GraphObject<*, *> && this::class == other::class && id == other.id)

    override fun hashCode(): Int = id.hashCode()
}
