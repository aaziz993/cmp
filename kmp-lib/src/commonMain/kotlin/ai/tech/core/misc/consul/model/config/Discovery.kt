package ai.tech.core.misc.consul.model.config

import ai.tech.core.misc.consul.client.catalog.model.ServiceWeights
import ai.tech.core.misc.model.config.EnabledConfig
import kotlin.time.Duration
import kotlinx.serialization.Serializable

@Serializable
public data class Discovery(
    val serviceName: String,
    val instanceId: String? = null,
    val address: String? = null,
    val preferIpAddress: Boolean = false,
    val header: Map<String, List<String>>? = null,
    val tags: List<String>? = null,
    val meta: Map<String, String>? = null,
    val serviceWeights: ServiceWeights? = null,
    val healthCheckPath: String = "/health",
    val healthCheckInterval: Duration,
    val healthCheckCriticalTimeout: Duration? = null,
    override val enable: Boolean = true
) : EnabledConfig
