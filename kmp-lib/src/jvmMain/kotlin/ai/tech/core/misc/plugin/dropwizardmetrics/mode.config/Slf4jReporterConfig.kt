package ai.tech.core.misc.plugin.dropwizardmetrics.mode.config

import ai.tech.core.misc.model.config.LogConfig
import com.codahale.metrics.MetricAttribute
import java.util.concurrent.TimeUnit
import kotlinx.serialization.Serializable

@Serializable
public data  class Slf4jReporterConfig(
    val logging: LogConfig? = null,
    val prefix: String? = null,
    val rateUnit: TimeUnit? = null,
    val durationUnit: TimeUnit? = null,
    val shutdownExecutorOnStop: Boolean? = null,
    val disabledMetricAttributes: Set<MetricAttribute>? = null,
    val start: ScheduledReporterStartConfig,
)
