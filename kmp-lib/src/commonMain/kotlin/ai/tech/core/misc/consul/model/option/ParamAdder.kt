package ai.tech.core.misc.consul.model.option

import ai.tech.core.misc.network.http.client.model.QueryAccessible

public interface ParamAdder : QueryAccessible {

    public val queryParameters: List<String>
        get() = emptyList()

    public val headers: Map<String, String>
        get() = emptyMap()
}
