package ai.tech.core.misc.consul.model.parameter

import ai.tech.core.misc.network.http.client.serializableHttpQueryParameters

public interface Parameters {

    public val query: Map<String, String>
        get() = serializableHttpQueryParameters

    public val queryParameters: List<String>
        get() = emptyList()

    public val headers: Map<String, String>
        get() = emptyMap()
}
