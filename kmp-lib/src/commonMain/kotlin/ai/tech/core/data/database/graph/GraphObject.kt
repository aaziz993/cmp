package ai.tech.core.data.database.graph

import ai.tech.core.data.database.CRUDRepository
import ai.tech.core.data.expression.f
import kotlin.coroutines.cancellation.CancellationException

public abstract class GraphObject<T : GraphObject<T, ID>, ID : Comparable<ID>>(
    public val id: ID? = null
) {

    @Suppress("UNUSED", "UNCHECKED_CAST")
    @Throws(CancellationException::class)
    public suspend fun CRUDRepository<T>.save(): Unit = insert(listOf(this as T))

    @Suppress("UNUSED")
    @Throws(CancellationException::class)
    public suspend fun CRUDRepository<T>.delete(): Boolean = delete("id".f eq id!!) > 0L

    override fun equals(other: Any?): Boolean =
        this === other || (other is GraphObject<*, *> && this::class == other::class && id == other.id)

    override fun hashCode(): Int = id.hashCode()
}
