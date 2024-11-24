package ai.tech.core.misc.plugin.micrometermetrics.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import io.micrometer.core.instrument.Clock
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.composite.CompositeMeterRegistry
import io.micrometer.core.instrument.logging.LoggingMeterRegistry
import io.micrometer.core.instrument.logging.LoggingRegistryConfig
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import kotlin.time.Duration
import kotlin.time.toJavaDuration
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
public data class RegistryConfig(
    val type: Registry,
    val prefix: String = type.name.lowercase(),
    val map: Map<String, String> = emptyMap(),
    override val enabled: Boolean = true,
) : EnabledConfig {

    @Transient
    val meterRegistry: MeterRegistry = when (type) {
        Registry.LOGGING -> LoggingMeterRegistry(
            object : LoggingRegistryConfig {
                override fun get(key: String): String? = map[key]

                override fun prefix(): String = prefix

                override fun step(): java.time.Duration = Duration.parse(get("$prefix.step") ?: "1m").toJavaDuration()
            },
            Clock.SYSTEM,
        )

        Registry.PROMETHEUS -> PrometheusMeterRegistry(
            object : PrometheusConfig {

                override fun get(key: String): String? = map[key]

                override fun prefix(): String = prefix

                override fun step(): java.time.Duration = Duration.parse(get("$prefix.step") ?: "1m").toJavaDuration()
            },
        )
    }
}

public val List<RegistryConfig>.meterRegistry: MeterRegistry
    get() = if (size == 1) {
        get(0).meterRegistry
    }
    else {
        CompositeMeterRegistry(Clock.SYSTEM, map { it.meterRegistry })
    }

