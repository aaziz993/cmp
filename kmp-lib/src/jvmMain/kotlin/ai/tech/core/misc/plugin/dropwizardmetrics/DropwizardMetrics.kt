package ai.tech.core.misc.plugin.dropwizardmetrics

import ai.tech.core.misc.model.config.EnabledConfig
import com.codahale.metrics.Slf4jReporter
import com.codahale.metrics.jmx.JmxReporter
import ai.tech.core.misc.plugin.dropwizardmetrics.model.config.DropwizardMetricsConfig
import io.ktor.server.application.*
import io.ktor.server.metrics.dropwizard.*

public fun Application.configureDropwizardMetrics(config: DropwizardMetricsConfig?, block: (io.ktor.server.metrics.dropwizard.DropwizardMetricsConfig.() -> Unit)? = null) {
    val configBlock: (io.ktor.server.metrics.dropwizard.DropwizardMetricsConfig.() -> Unit)? = config?.takeIf(EnabledConfig::enabled)?.let {
        {
            it.baseName?.let { baseName = it }
            it.registerJvmMetricSets?.let { registerJvmMetricSets = it }

            it.slf4jReporter?.takeIf(EnabledConfig::enabled)?.let {
                val slf4jReporter = Slf4jReporter.forRegistry(registry)

                it.logging?.takeIf(EnabledConfig::enabled)?.let {
                    it.level?.let {
                        slf4jReporter.withLoggingLevel(Slf4jReporter.LoggingLevel.valueOf(it))
                    }
                }
                it.prefix?.let { slf4jReporter.prefixedWith(it) }
                it.rateUnit?.let { slf4jReporter.convertRatesTo(it) }
                it.durationUnit?.let { slf4jReporter.convertDurationsTo(it) }
                it.shutdownExecutorOnStop?.let { slf4jReporter.shutdownExecutorOnStop(it) }
                it.disabledMetricAttributes?.let { slf4jReporter.disabledMetricAttributes(it) }

                slf4jReporter
                    .build()
                    .start(it.start.initialDelay, it.start.period, it.start.unit)
            }

            it.jmxReporter?.takeIf(EnabledConfig::enabled)?.let {
                val jmxReporter = JmxReporter.forRegistry(registry)

                it.rateUnit?.let { jmxReporter.convertRatesTo(it) }
                it.durationUnit?.let { jmxReporter.convertDurationsTo(it) }
                it.domain?.let { jmxReporter.inDomain(it) }
                it.specificDurationUnits?.let { jmxReporter.specificDurationUnits(it) }
                it.specificRateUnits?.let { jmxReporter.specificRateUnits(it) }

                jmxReporter
                    .build()
                    .start()
            }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(DropwizardMetrics) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
