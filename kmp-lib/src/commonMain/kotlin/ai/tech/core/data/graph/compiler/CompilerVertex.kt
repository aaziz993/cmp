package ai.tech.core.data.graph.compiler

import ai.tech.core.data.database.graph.Vertex

public class CompilerVertex<ID : Comparable<ID>, EID : Comparable<EID>>(
    id: ID? = null,
    public val type: CompilerVertexType,
    public val payload: Any? = null,
) : Vertex<CompilerVertex<ID, EID>, ID, CompilerEdge<EID, ID>, EID>(id)
