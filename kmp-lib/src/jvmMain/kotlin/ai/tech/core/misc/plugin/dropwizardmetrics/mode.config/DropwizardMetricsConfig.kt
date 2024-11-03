package ai.tech.core.misc.plugin.dropwizardmetrics.mode.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class DropwizardMetricsConfig(
    val baseName: String? = null,
    val registerJvmMetricSets: Boolean? = null,
    val slf4jReporter: Slf4jReporterConfig? = null,
    val jmxReporter: JmxReporterConfig? = null,
    override val enable: Boolean? = null,
) : EnabledConfig
