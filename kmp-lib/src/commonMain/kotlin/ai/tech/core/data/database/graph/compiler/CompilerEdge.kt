package ai.tech.core.data.database.graph.compiler

import ai.tech.core.data.database.CRUDRepository
import ai.tech.core.data.database.graph.Edge

public class CompilerEdge<ID : Comparable<ID>, VID : Comparable<VID>>(
    id: ID?,
    fromVertexId: VID,
    toVertexId: VID,
    public val type: CompilerEdgeType,
    public val payload: Any,
    edgeObjects: CRUDRepository<CompilerEdge<ID, VID>>? = null,
) : Edge<CompilerEdge<ID, VID>, ID, CompilerVertex<VID, ID>, VID>(
    id,
    fromVertexId,
    toVertexId,
    edgeObjects
)