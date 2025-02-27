package ai.tech.core.data.graph

import ai.tech.core.data.crud.CRUDRepository

public open class Graph<V : Vertex<V, VID, E, EID>, VID : Comparable<VID>, E : Edge<E, EID, V, VID>, EID : Comparable<EID>>(
    public val verticesRepository: CRUDRepository<V>,
    public val edgesRepository: CRUDRepository<E>
)
