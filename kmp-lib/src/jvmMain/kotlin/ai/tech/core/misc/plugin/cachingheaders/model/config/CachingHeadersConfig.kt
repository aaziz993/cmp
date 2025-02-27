package ai.tech.core.misc.plugin.cachingheaders.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class CachingHeadersConfig(
    public val rootOption: CacheControlConfig? = null,
    public val options: Set<CacheContentTypeOptionConfig>? = null,
    override val enabled: Boolean = true,
) : EnabledConfig
