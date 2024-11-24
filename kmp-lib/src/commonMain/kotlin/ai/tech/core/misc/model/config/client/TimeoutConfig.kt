package ai.tech.core.misc.model.config.client

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class TimeoutConfig(
    val requestTimeoutMillis: Long? = null,
    val connectTimeoutMillis: Long? = null,
    val socketTimeoutMillis: Long? = null,
    override val enabled: Boolean = true
) : EnabledConfig
