package ai.tech.core.data.database.graph

import ai.tech.core.data.database.CRUDRepository
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.f
import kotlinx.coroutines.flow.firstOrNull

public abstract class Edge<E : Edge<E, ID, V, VID>, ID : Comparable<ID>, V : Vertex<V, VID, E, ID>, VID : Comparable<VID>>(
    id: ID?,
    public val fromVertexId: VID,
    public val toVertexId: VID,
) : GraphObject<E, ID>(id) {

    public suspend fun CRUDRepository<V>.fromVertex(predicate: BooleanVariable? = null): V? = find(
        predicate = "id".f.eq(fromVertexId).let {
            if (predicate == null) {
                it
            }
            else {
                it.and(predicate)
            }
        },
    ).firstOrNull()

    public suspend fun Graph<V, VID, E, ID>.fromVertex(predicate: BooleanVariable? = null): V? = vertices.fromVertex(predicate)

    public suspend fun CRUDRepository<V>.toVertex(predicate: BooleanVariable? = null): V? = find(
        predicate = "id".f.eq(toVertexId).let {
            if (predicate == null) {
                it
            }
            else {
                it.and(predicate)
            }
        },
    ).firstOrNull()

    public suspend fun Graph<V, VID, E, ID>.toVertex(predicate: BooleanVariable? = null): V? = vertices.toVertex(predicate)
}
