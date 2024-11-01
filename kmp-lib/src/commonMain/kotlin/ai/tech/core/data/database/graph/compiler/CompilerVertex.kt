package ai.tech.core.data.database.graph.compiler

import ai.tech.core.data.database.CRUDRepository
import ai.tech.core.data.database.graph.Vertex

public class CompilerVertex<ID : Comparable<ID>, EID : Comparable<EID>>(
    id: ID? = null,
    public val type: CompilerVertexType,
    public val payload: Any? = null,
    vertexObjects: CRUDRepository<CompilerVertex<ID, EID>>? = null,
) : Vertex<CompilerVertex<ID, EID>, ID, CompilerEdge<EID, ID>, EID>(id, vertexObjects)