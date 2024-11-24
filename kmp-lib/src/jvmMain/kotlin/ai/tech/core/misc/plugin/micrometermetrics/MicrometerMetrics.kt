package ai.tech.core.misc.plugin.micrometermetrics

import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.micrometermetrics.model.config.MicrometerMetricsConfig
import ai.tech.core.misc.plugin.micrometermetrics.model.config.meterRegistry
import io.ktor.server.application.*
import io.ktor.server.metrics.micrometer.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.jvm.ClassLoaderMetrics
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics
import io.micrometer.core.instrument.binder.system.ProcessorMetrics
import io.micrometer.core.instrument.binder.system.UptimeMetrics
import io.micrometer.core.instrument.distribution.DistributionStatisticConfig
import java.time.Duration
import kotlin.time.DurationUnit

public fun Application.configureMicrometerMetrics(
    config: MicrometerMetricsConfig?,
    block: (io.ktor.server.metrics.micrometer.MicrometerMetricsConfig.(MeterRegistry?) -> MeterRegistry)? = null
): Set<MeterRegistry> {

    var configBlock: (io.ktor.server.metrics.micrometer.MicrometerMetricsConfig.() -> MeterRegistry)? =

        config?.takeIf(EnabledConfig::enabled)?.let {
            // After installing MicrometerMetrics, you need to create a registry for your monitoring system and assign it to the registry property.
            // Below, the MeterRegistry is created outside the installation block to have the capability to reuse this registry in different route handlers

            val micrometerRegistry: MeterRegistry = it.registries.meterRegistry

            {
                registry = micrometerRegistry

                it.metricName?.let { metricName = it }

                it.distinctNotRegisteredRoutes?.let { distinctNotRegisteredRoutes = it }

                meterBinders = listOfNotNull(
                    if (it.classLoaderMetrics == true) ClassLoaderMetrics() else null,
                    if (it.jvmMemoryMetrics == true) JvmMemoryMetrics() else null,
                    if (it.jvmGcMetrics == true) JvmGcMetrics() else null,
                    if (it.processorMetrics == true) ProcessorMetrics() else null,
                    if (it.jvmThreadMetrics == true) JvmThreadMetrics() else null,
                    if (it.fileDescriptorMetrics == true) FileDescriptorMetrics() else null,
                    if (it.uptimeMetrics == true) UptimeMetrics() else null,
                )

                it.distributionStatistics?.takeIf(EnabledConfig::enabled)?.let {

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

                micrometerRegistry
            }
        }

    if (configBlock == null && block == null) {
        return emptySet()
    }

    var meterRegistries = emptySet<MeterRegistry>()

    install(MicrometerMetrics) {
        val meterRegistry = configBlock?.invoke(this)

        meterRegistries = setOfNotNull(
            meterRegistry,
            block?.invoke(this, meterRegistry),
        )
    }

    return meterRegistries
}
