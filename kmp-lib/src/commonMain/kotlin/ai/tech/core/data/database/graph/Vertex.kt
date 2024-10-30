package ai.tech.core.data.database.graph

import ai.tech.core.data.database.CRUDRepository
import ai.tech.core.data.database.graph.model.GraphException
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.f
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.cancellation.CancellationException

@Suppress("UNUSED")
public abstract class Vertex<V : Vertex<V, ID, E, EID>, ID : Comparable<ID>, E : Edge<E, EID, V, ID>, EID : Comparable<EID>>(
    id: ID? = null,
    vertexObjects: CRUDRepository<V>? = null,
) : GraphObject<V, ID>(id, vertexObjects) {
    protected var edgeObjects: CRUDRepository<E>? = null

    internal fun assignEdgeResources(edgeObjects: CRUDRepository<E>) {
        this.edgeObjects = edgeObjects
    }

    @Suppress("UNUSED")
    @Throws(GraphException::class, CancellationException::class)
    public suspend fun edges(predicate: BooleanVariable? = null): Flow<E> {
        if (id == null) {
            throw GraphException("Id is not provided")
        }

        if (graphObjects == null) {
            throw GraphException("Graph objects is not provided")
        }

        val idPredicate = "fromVertexId".f.eq(id).or("toVertexId".f.eq(id))

        return (edgeObjects ?: throw GraphException("Edge graph objects is not provided")).find(
            predicate = if (predicate == null)
                idPredicate else idPredicate.and(predicate)
        ).onEach {
            it.assignStartVertexResources(id, graphObjects)
        }
    }
}