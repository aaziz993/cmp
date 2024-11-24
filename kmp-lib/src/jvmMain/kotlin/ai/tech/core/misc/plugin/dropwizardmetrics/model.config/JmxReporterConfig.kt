package ai.tech.core.misc.plugin.dropwizardmetrics.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import java.util.concurrent.TimeUnit
import kotlinx.serialization.Serializable

@Serializable
public data class JmxReporterConfig(
    val rateUnit: TimeUnit? = null,
    val durationUnit: TimeUnit? = null,
    val domain: String? = null,
    val specificDurationUnits: Map<String, TimeUnit>? = null,
    val specificRateUnits: Map<String, TimeUnit>? = null,
    override val enabled: Boolean = true,
) : EnabledConfig
