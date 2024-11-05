package ai.tech.core.misc.model.config.presentation

import ai.tech.core.misc.type.toMap
import kotlinx.serialization.json.Json

public interface DestinationsConfig {
    private val destinations: Map<String, DestinationConfig>
        get() = Json.Default.toMap(this)

    public operator fun get(route: String): DestinationConfig = destinations[route]!!
}
