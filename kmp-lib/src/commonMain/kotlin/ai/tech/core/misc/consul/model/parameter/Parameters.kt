package ai.tech.core.misc.consul.model.parameter

import ai.tech.core.misc.network.http.client.model.QueryAccessible

public interface Parameters : QueryAccessible {

    public val queryParameters: List<String>
        get() = emptyList()

    public val headers: Map<String, String>
        get() = emptyMap()
}
