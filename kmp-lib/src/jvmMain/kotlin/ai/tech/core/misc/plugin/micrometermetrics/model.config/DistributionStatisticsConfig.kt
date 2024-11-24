package ai.tech.core.misc.plugin.micrometermetrics.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlin.time.Duration
import kotlinx.serialization.Serializable

@Serializable
public data class DistributionStatisticsConfig(
    val percentileHistogram: Boolean? = null,
    val percentiles: List<Double>? = null,
    val percentilePrecision: Int? = null,
    val serviceLevelObjectives: List<Double>? = null,
    val minimumExpectedValue: Double? = null,
    val maximumExpectedValue: Double? = null,
    val expiry: Duration? = null,
    val bufferLength: Int? = null,
    override val enabled: Boolean = true,
) : EnabledConfig
