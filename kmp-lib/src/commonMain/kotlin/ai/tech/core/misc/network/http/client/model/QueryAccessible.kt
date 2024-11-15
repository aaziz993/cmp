package ai.tech.core.misc.network.http.client.model;

import ai.tech.core.misc.type.serializableProperties
import ai.tech.core.misc.type.serializer.encodeAnyToString
import kotlinx.serialization.json.Json

public interface QueryAccessible {

    public val query: Map<String, String>
        get() = serializableProperties.filterValues { it != null }.mapValues { Json.Default.encodeAnyToString(it.value) }
}
