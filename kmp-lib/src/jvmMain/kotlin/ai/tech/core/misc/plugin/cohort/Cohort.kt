package ai.tech.core.misc.plugin.cohort

import ai.tech.core.data.database.model.config.DBProviderConfig
import ai.tech.core.data.database.model.config.hikariDataSource
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.auth.oauth.model.config.ServerOAuthConfig
import ai.tech.core.misc.plugin.cohort.model.config.CohortConfig
import com.sksamuel.cohort.Cohort
import com.sksamuel.cohort.HealthCheckRegistry
import com.sksamuel.cohort.db.DatabaseConnectionHealthCheck
import com.sksamuel.cohort.endpoints.CohortConfiguration
import com.sksamuel.cohort.healthcheck.http.EndpointHealthCheck
import io.ktor.client.request.*
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers

public fun Application.configureCohort(
    config: CohortConfig?,
    oauthConfig: Map<String?, ServerOAuthConfig> = emptyMap(),
    databaseConfig: Map<String?, DBProviderConfig> = emptyMap(),
    block: (CohortConfiguration.() -> Map<String, String>)? = null
): Map<String, String> {

    val configBlock: (CohortConfiguration.() -> Map<String, String>)? = config?.takeIf(EnabledConfig::enable)?.let {
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

            // enable health checks for kubernetes
            // each of these is optional and can map to any healthcheck url you wish
            // for example if you just want a single health endpoint, you could use /health
            oauthConfig.filterValues(EnabledConfig::enable).map { (name, config) ->
                it.getOAuthEndpoint(name).also { (name, endpoint) ->
                    healthcheck(
                        endpoint,
                        HealthCheckRegistry(Dispatchers.Default) {
                            register(
                                EndpointHealthCheck { it.get(config.address) },
                            )
                        },
                    )
                }
            }.toMap() + databaseConfig.filterValues(EnabledConfig::enable).map { (name, config) ->
                it.getDBEndpoint(name).also { (_, endpoint) ->
                    HealthCheckRegistry(Dispatchers.Default) {
                        register(endpoint, DatabaseConnectionHealthCheck(config.connection.hikariDataSource))
                    }
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





