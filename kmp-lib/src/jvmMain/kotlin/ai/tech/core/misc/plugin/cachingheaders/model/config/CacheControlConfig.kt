package ai.tech.core.misc.plugin.cachingheaders.model.config

import io.ktor.http.*
import kotlinx.serialization.Serializable

@Serializable
public data class CacheControlConfig(
    val type: CacheControlType,
    val maxAgeSeconds: Int,
    val proxyMaxAgeSeconds: Int? = null,
    val mustRevalidate: Boolean = false,
    val proxyRevalidate: Boolean = false,
    val visibility: CacheControl.Visibility? = null,
){
    public fun cacheControl(): CacheControl = when (type) {
        CacheControlType.NO_CACHE -> CacheControl.NoCache(this.visibility)
        CacheControlType.NO_STORE -> CacheControl.NoStore(this.visibility)
        CacheControlType.MAX_AGE -> CacheControl.MaxAge(
            this.maxAgeSeconds,
            this.proxyMaxAgeSeconds,
            this.mustRevalidate,
            this.proxyRevalidate,
            this.visibility
        )
    }
}
