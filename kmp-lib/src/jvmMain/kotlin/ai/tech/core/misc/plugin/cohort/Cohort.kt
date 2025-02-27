package ai.tech.core.misc.plugin.cohort

import ai.tech.core.data.database.model.config.DBConfig
import ai.tech.core.data.database.model.config.hikariDataSource
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.auth.oauth.model.config.ServerOAuthConfig
import ai.tech.core.misc.plugin.cohort.model.config.CohortConfig
import com.sksamuel.cohort.Cohort
import com.sksamuel.cohort.HealthCheckRegistry
import com.sksamuel.cohort.db.DatabaseConnectionHealthCheck
import com.sksamuel.cohort.endpoints.CohortConfiguration
import com.sksamuel.cohort.healthcheck.http.EndpointHealthCheck
import com.sksamuel.cohort.logback.LogbackManager
import com.sksamuel.cohort.micrometer.CohortMetrics
import io.ktor.client.request.*
import io.ktor.server.application.*
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.MeterBinder

public fun Application.configureCohort(
    config: CohortConfig?,
    registries: Set<MeterRegistry> = emptySet(),
    oauthConfig: Map<String?, ServerOAuthConfig> = emptyMap(),
    databaseConfig: Map<String?, DBConfig> = emptyMap(),
    block: (CohortConfiguration.() -> Map<String, String>)? = null
): Map<String, String> {

    val configBlock: (CohortConfiguration.() -> Map<String, String>)? = config?.takeIf(EnabledConfig::enabled)?.let {
        {
            require(it.endpointPrefix.isNotEmpty()) {
                "Property endpointPrefix can't be empty"
            }

            // enable an endpoint to dump the heap in hprof format
            it.heapDump?.let { heapDump = it }

            // enable an endpoint to display operating system name and version
            it.operatingSystem?.let { operatingSystem = it }

            it.memory?.let { memory = it }

            // enable runtime JVM information such as vm options and vendor name
            it.jvmInfo?.let { jvmInfo = it }

            it.gc?.let { gc = it }

            // enable an endpoint to dump threads
            it.threadDump?.let { threadDump = it }

            // show current system properties
            it.sysprops?.let { sysprops = it }

            endpointPrefix = it.endpointPrefix

            // Once instance of LogManager passed to logManager, the endpoint GET /{endpointPrefix}/logging can be used to show current log information and PUT /{endpointPrefix}/logging/{name}/{level} can be used to modify a log level at runtime
            logManager = LogbackManager

            // enable health checks for kubernetes
            // each of these is optional and can map to any healthcheck url you wish
            // for example if you just want a single health endpoint, you could use /health
            oauthConfig.filterValues(EnabledConfig::enabled).map { (name, config) ->
                it.getOAuthEndpoint(name).also { (_, endpoint) ->
                    healthcheck(
                        endpoint,
                        HealthCheckRegistry {
                            register(
                                EndpointHealthCheck { it.get(config.address) },
                            )
                        }.also { heathCheck -> { CohortMetrics(heathCheck).bindTo(registries) } },
                    )
                }
            }.toMap() + databaseConfig.filterValues(EnabledConfig::enabled).map { (name, config) ->
                it.getDBEndpoint(name).also { (_, endpoint) ->
                    HealthCheckRegistry {
                        register(endpoint, DatabaseConnectionHealthCheck(config.hikariDataSource))
                    }.also { heathCheck -> CohortMetrics(heathCheck).bindTo(registries) }
                }
            }.toMap()
        }
    }

    if (configBlock == null && block == null) {
        return emptyMap()
    }

    var healthChecks: Map<String, String> = emptyMap()

    install(Cohort) {
        healthChecks = configBlock?.invoke(this).orEmpty() + block?.invoke(this).orEmpty()
    }

    return healthChecks
}

public fun MeterBinder.bindTo(registries: Iterable<MeterRegistry>) = registries.forEach(::bindTo)

