package ai.tech.core.misc.plugin.micrometermetrics.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class MicrometerMetricsConfig(
    val type: MicrometerMetrics,
    val metricName: String? = null,
    val distinctNotRegisteredRoutes: Boolean? = null,
    val classLoaderMetrics: Boolean? = null,
    val jvmMemoryMetrics: Boolean? = null,
    val jvmGcMetrics: Boolean? = null,
    val processorMetrics: Boolean? = null,
    val jvmThreadMetrics: Boolean? = null,
    val fileDescriptorMetrics: Boolean? = null,
    val distributionStatistics: DistributionStatisticsConfig? = null,
    override val enable: Boolean? = null
) : EnabledConfig