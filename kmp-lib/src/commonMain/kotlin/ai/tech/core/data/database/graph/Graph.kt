package ai.tech.core.data.database.graph

import ai.tech.core.data.database.CRUDRepository

@Suppress("UNUSED")
public interface Graph<V : Vertex<V, VID, E, EID>, VID : Comparable<VID>, E : Edge<E, EID, V, VID>, EID : Comparable<EID>> {
    public fun vertices(): CRUDRepository<V>
    public fun edges(): CRUDRepository<E>
}