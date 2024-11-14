package ai.tech.core.misc.network.http.client.model;

import ai.tech.core.misc.type.accessor.model.Accessible
import ai.tech.core.misc.type.encodeAnyToString
import kotlinx.serialization.json.Json

public interface QueryAccessible : Accessible {

    public val query: Map<String, String>
        get() = properties.filterValues { it != null }.mapValues { Json.Default.encodeAnyToString(it.value) }
}
