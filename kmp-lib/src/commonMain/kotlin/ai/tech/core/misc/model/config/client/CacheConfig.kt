package ai.tech.core.misc.model.config.client

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class CacheConfig(
    val isShared: Boolean? = null,
    override val enable: Boolean = true,
) : EnabledConfig
