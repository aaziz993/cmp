package ai.tech.core.misc.plugin.dropwizardmetrics.mode.config

import java.util.concurrent.TimeUnit
import kotlinx.serialization.Serializable

@Serializable
public data  class ScheduledReporterStartConfig(
    val period: Long,
    val initialDelay: Long = period,
    val unit: TimeUnit,
)
