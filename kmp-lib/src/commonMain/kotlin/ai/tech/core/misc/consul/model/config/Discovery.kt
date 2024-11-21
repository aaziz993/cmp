package ai.tech.core.misc.consul.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlin.time.Duration
import kotlinx.serialization.Serializable

@Serializable
public data class Discovery(
    val serviceName: String,
    val instanceId: String? = null,
    val preferIpAddress: Boolean = false,
    val tags: List<String> = emptyList(),
    val meta: Map<String, String>? = null,
    val header: Map<String, List<String>>? = null,
    val healthCheckPath: String = "/health",
    val healthCheckInterval: Duration,
    val healthCheckCriticalTimeout: Duration? = null,
    override val enable: Boolean = true
) : EnabledConfig
