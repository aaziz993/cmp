package ai.tech.core.misc.plugin.micrometermetrics

import ai.tech.core.misc.plugin.micrometermetrics.model.config.MicrometerMetrics.*
import ai.tech.core.misc.plugin.micrometermetrics.model.config.MicrometerMetricsConfig
import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import java.time.Duration
import kotlin.time.DurationUnit

public fun Application.configureMicrometerMetrics(config: MicrometerMetricsConfig?, block: (io.ktor.server.metrics.micrometer.MicrometerMetricsConfig.() -> Unit)? = null) {

    var configBlock: (io.ktor.server.metrics.micrometer.MicrometerMetricsConfig.() -> Unit)? = null

    config?.takeIf { it.enable != false }?.let {
        val appMicrometerRegistry: MeterRegistry = when (it.type) {
            PROMETHEUS -> {
                PrometheusMeterRegistry(PrometheusConfig.DEFAULT).also {
                    routing {
                        get("/metrics") {
                            call.respond(it.scrape())
                        }
                    }
                }
            }
        }

        configBlock = {
            registry = appMicrometerRegistry

            it.metricName?.let { metricName = it }

            it.distinctNotRegisteredRoutes?.let { distinctNotRegisteredRoutes = it }

            meterBinders = listOfNotNull(
                if (it.classLoaderMetrics == true) ClassLoaderMetrics() else null,
                if (it.jvmMemoryMetrics == true) JvmMemoryMetrics() else null,
                if (it.jvmGcMetrics == true) JvmGcMetrics() else null,
                if (it.processorMetrics == true) ProcessorMetrics() else null,
                if (it.jvmThreadMetrics == true) JvmThreadMetrics() else null,
                if (it.fileDescriptorMetrics == true) FileDescriptorMetrics() else null,
            )

            it.distributionStatistics?.let {

                val distributionStatisticConfigBuilder = DistributionStatisticConfig.Builder()

                it.percentileHistogram?.let {
                    if (it) {
                        distributionStatisticConfigBuilder.percentilesHistogram(it)
                    }
                }
                it.percentiles?.let { distributionStatisticConfigBuilder.percentiles(*it.toDoubleArray()) }
                it.percentilePrecision?.let { distributionStatisticConfigBuilder.percentilePrecision(it) }
                it.serviceLevelObjectives?.let { distributionStatisticConfigBuilder.serviceLevelObjectives(*it.toDoubleArray()) }
                it.minimumExpectedValue?.let { distributionStatisticConfigBuilder.minimumExpectedValue(it) }
                it.maximumExpectedValue?.let { distributionStatisticConfigBuilder.maximumExpectedValue(it) }
                it.expiry?.let { distributionStatisticConfigBuilder.expiry(Duration.ofNanos(it.toLong(DurationUnit.NANOSECONDS))) }
                it.bufferLength?.let { distributionStatisticConfigBuilder.bufferLength(it) }

                distributionStatisticConfig = distributionStatisticConfigBuilder.build()
            }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(MicrometerMetrics) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
