package ai.tech.core.data.database.graph.compiler

public abstract class GraphCompiler<VID : Comparable<VID>, EID : Comparable<EID>>(
    public val graph: CompilerGraph<VID, EID>) {

    public abstract suspend fun compile()
}
