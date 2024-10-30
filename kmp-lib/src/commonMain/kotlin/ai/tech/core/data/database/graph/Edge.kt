package ai.tech.core.data.database.graph

import ai.tech.core.data.database.CRUDRepository
import ai.tech.core.data.database.graph.model.GraphException
import ai.tech.core.data.expression.f
import kotlinx.coroutines.flow.firstOrNull
import kotlin.coroutines.cancellation.CancellationException

@Suppress("UNUSED")
public abstract class Edge<E : Edge<E, ID, V, VID>, ID : Comparable<ID>, V : Vertex<V, VID, E, ID>, VID : Comparable<VID>>(
    id: ID?,
    public val fromVertexId: VID,
    public val toVertexId: VID,
    edgeObjects: CRUDRepository<E>? = null,
) : GraphObject<E, ID>(id, edgeObjects) {
    protected var startVertexId: VID? = null
    protected var vertexObjects: CRUDRepository<V>? = null

    internal fun assignStartVertexResources(
        startVertexId: VID,
        vertexObjects: CRUDRepository<V>
    ) {
        this.startVertexId = startVertexId
        this.vertexObjects = vertexObjects
    }

    @Throws(GraphException::class)
    public fun isStartVertexFrom(): Boolean =
        (startVertexId ?: throw GraphException("Start vertex id is not provided")) == fromVertexId

    @Suppress("UNUSED")
    @Throws(GraphException::class)
    public fun isStartVertexTo(): Boolean = !isStartVertexFrom()

    @Suppress("UNUSED")
    @Throws(GraphException::class, CancellationException::class)
    public suspend fun endVertex(): V? {
        if (graphObjects == null) {
            throw GraphException("Graph objects is not provided")
        }

        return (vertexObjects ?: throw GraphException("Vertex graph objects is not provided"))
            .find(predicate = "id".f.eq(if (isStartVertexFrom()) toVertexId else fromVertexId)).firstOrNull()
            ?.apply { assignEdgeResources(graphObjects) }
    }
}