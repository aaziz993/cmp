package ai.tech.core.data.database.graph

import ai.tech.core.data.database.CRUDRepository
import ai.tech.core.data.database.graph.model.GraphException
import ai.tech.core.data.expression.f
import kotlin.coroutines.cancellation.CancellationException

public abstract class GraphObject<T : GraphObject<T, ID>, ID : Comparable<ID>>(
    public val id: ID? = null,
    protected val graphObjects: CRUDRepository<T>? = null
) {
    @Suppress("UNUSED", "UNCHECKED_CAST")
    @Throws(GraphException::class, CancellationException::class)
    public suspend fun save(): T {
        if (graphObjects == null) {
            throw GraphException("Graph objects is not provided")
        }

        return graphObjects.insert(listOf(this as T)).let { this }
    }

    @Suppress("UNUSED")
    @Throws(GraphException::class, CancellationException::class)
    public suspend fun delete(): Boolean {
        if (graphObjects == null) {
            throw GraphException("${this::class.simpleName} graph objects is not provided")
        }

        return graphObjects.delete("id".f eq id!!) > 0L
    }

    override fun equals(other: Any?): Boolean =
        this === other || (
                other != null && this::class == other::class &&
                        id == (other as GraphObject<*, *>).id)

    override fun hashCode(): Int = id.hashCode()
}