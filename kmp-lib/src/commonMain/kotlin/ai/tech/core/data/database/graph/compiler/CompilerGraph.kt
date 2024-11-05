package ai.tech.core.data.database.graph.compiler

import ai.tech.core.data.database.crud.CRUDRepository
import ai.tech.core.data.database.graph.Graph

public class CompilerGraph<VID : Comparable<VID>, EID : Comparable<EID>>(
    verticesRepository: CRUDRepository<CompilerVertex<VID, EID>>,
    edgesRepository: CRUDRepository<CompilerEdge<EID, VID>>
) : Graph<CompilerVertex<VID, EID>, VID, CompilerEdge<EID, VID>, EID>(
    verticesRepository,
    edgesRepository,
)
