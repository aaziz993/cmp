package ai.tech.core.data.database.graph

import ai.tech.core.data.database.CRUDRepository
import ai.tech.core.data.database.graph.model.GraphException
import ai.tech.core.data.expression.f
import kotlinx.coroutines.flow.firstOrNull

public abstract class Edge<E : Edge<E, ID, V, VID>, ID : Comparable<ID>, V : Vertex<V, VID, E, ID>, VID : Comparable<VID>>(
    id: ID?,
    public val fromVertexId: VID,
    public val toVertexId: VID,
) : GraphObject<E, ID>(id) {

    public suspend fun CRUDRepository<V>.fromVertex(): V? = find(predicate = "id".f.eq(fromVertexId)).firstOrNull()

    public suspend fun CRUDRepository<V>.toVertex(): V? = find(predicate = "id".f.eq(toVertexId)).firstOrNull()
}
