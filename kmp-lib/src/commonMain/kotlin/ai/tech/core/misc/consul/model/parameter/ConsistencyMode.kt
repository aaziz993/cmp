package ai.tech.core.misc.consul.model.parameter

import arrow.core.fold
import kotlinx.serialization.Serializable

@Serializable
public data class ConsistencyMode(
    val name: String,
    val ordinal: Int,
    val param: String? = null,
    val additionalHeaders: Map<String, String> = emptyMap()
) {

    override fun toString(): String = additionalHeaders.fold(name) { acc, (k, v) -> "[$k=$v]" }

    public companion object {

        public val DEFAULT: ConsistencyMode = ConsistencyMode("DEFAULT", 0, null)
        public val STALE: ConsistencyMode = ConsistencyMode("STALE", 1, "stale")
        public val CONSISTENT: ConsistencyMode = ConsistencyMode("CONSISTENT", 2, "consistent")

        public fun createCachedConsistencyWithMaxAgeAndStale(
            maxAgeInSeconds: Long? = null,
            maxStaleInSeconds: Long? = null
        ): ConsistencyMode {
            var maxAge = ""

            maxAgeInSeconds?.let {
                require(it >= 0) { "maxAgeInSeconds must greater or equal to 0" }
                maxAge += "max-age=$it"
            }

            maxStaleInSeconds?.let {
                require(it >= 0) { "maxStaleInSeconds must greater or equal to 0" }
                if (!maxAge.isEmpty()) {
                    maxAge += ","
                }
                maxAge += "stale-if-error=$it"
            }

            val headers = if (maxAge.isEmpty()) {
                emptyMap()
            }
            else {
                mapOf("Cache-Control" to maxAge)
            }

            return ConsistencyMode("CACHED", 3, "cached", headers)
        }

        public fun values(): List<ConsistencyMode> = listOf(
            DEFAULT,
            STALE,
            CONSISTENT,
            // Don't push CACHED as it is just to keep backward compatibility
        )
    }
}
