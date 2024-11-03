package ai.tech.core.data.database.graph

import ai.tech.core.data.database.CRUDRepository

public data class Graph<V : Vertex<V, VID, E, EID>, VID : Comparable<VID>, E : Edge<E, EID, V, VID>, EID : Comparable<EID>>(
    public val vertices: CRUDRepository<V>,
    public val edges: CRUDRepository<E>
)

//context(CRUDRepository<V>)
public fun <V : Vertex<V, VID, E, EID>, VID : Comparable<VID>, E : Edge<E, EID, V, VID>, EID : Comparable<EID>> withGraph(){

}
