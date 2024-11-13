package ai.tech.core.data.graph

import ai.tech.core.data.crud.CRUDRepository
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.f
import kotlinx.coroutines.flow.Flow

public abstract class Vertex<V : Vertex<V, ID, E, EID>, ID : Comparable<ID>, E : Edge<E, EID, V, ID>, EID : Comparable<EID>>(
    id: ID? = null,
) : GraphEntity<V, ID>(id) {

    public suspend fun CRUDRepository<E>.fromEdges(predicate: BooleanVariable? = null): Flow<E> = find(
        predicate = "fromVertexId".f.eq(id).let {
            if (predicate == null) {
                it
            }
            else {
                it.and(predicate)
            }
        },
    )

    public suspend fun Graph<V, ID, E, EID>.fromEdges(predicate: BooleanVariable? = null): Flow<E> = edgesRepository.fromEdges(predicate)

    public suspend fun CRUDRepository<E>.toEdges(predicate: BooleanVariable? = null): Flow<E> = find(
        predicate = "toVertexId".f.eq(id).let {
            if (predicate == null) {
                it
            }
            else {
                it.and(predicate)
            }
        },
    )

    public suspend fun Graph<V, ID, E, EID>.toEdges(predicate: BooleanVariable? = null): Flow<E> = edgesRepository.toEdges(predicate)

    public suspend fun CRUDRepository<E>.edges(predicate: BooleanVariable? = null): Flow<E> = find(
        predicate = "fromVertexId".f.eq(id).or("toVertexId".f.eq(id)).let {
            if (predicate == null) {
                it
            }
            else {
                it.and(predicate)
            }
        },
    )

    public suspend fun Graph<V, ID, E, EID>.edges(predicate: BooleanVariable? = null): Flow<E> = edgesRepository.edges(predicate)
}
