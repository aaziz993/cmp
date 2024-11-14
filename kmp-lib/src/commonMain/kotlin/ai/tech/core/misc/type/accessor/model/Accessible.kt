package ai.tech.core.misc.type.accessor.model;

import ai.tech.core.misc.type.serializer.encodeToAny
import kotlinx.serialization.json.Json

public interface Accessible {

    @Suppress("UNCHECKED_CAST")
    public val properties: Map<String, Any?>
        get() = Json.Default.encodeToAny(this) as Map<String, *>
}
