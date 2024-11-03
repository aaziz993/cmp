package ai.tech.core.data.database.graph.compiler

import ai.tech.core.data.database.CRUDRepository
import ai.tech.core.data.database.graph.Graph

public class CompilerGraph<VID : Comparable<VID>, EID : Comparable<EID>>(
    vertices: CRUDRepository<CompilerVertex<VID, EID>>,
    edges: CRUDRepository<CompilerEdge<EID, VID>>
) : Graph<CompilerVertex<VID, EID>, VID, CompilerEdge<EID, VID>, EID>(
    vertices,
    edges,
)
