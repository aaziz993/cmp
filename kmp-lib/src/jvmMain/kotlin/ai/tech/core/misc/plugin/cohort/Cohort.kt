package ai.tech.core.misc.plugin.cohort

import ai.tech.core.data.database.model.config.DBProviderConfig
import ai.tech.core.data.database.model.config.createHikariDataSource
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
    oauthConfig: Map<String, ServerOAuthConfig>?,
    databaseConfig: Map<String, DBProviderConfig>?,
    block: (CohortConfiguration.() -> Unit)? = null) {

    val configBlock: (CohortConfiguration.() -> Unit)? = config?.takeIf(EnabledConfig::enable)?.let {
        {
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
            oauthConfig?.filterValues(EnabledConfig::enable)?.forEach { name, config ->
                healthcheck(
                    "oauth",
                    HealthCheckRegistry(Dispatchers.Default) {
                        register(
                            EndpointHealthCheck { it.get(config.address) },
                        )
                    },
                )
            }

            databaseConfig?.filterValues(EnabledConfig::enable)?.forEach { name, config ->
                HealthCheckRegistry(Dispatchers.Default) {
                    register(name, DatabaseConnectionHealthCheck(config.connection.hikariDataSource))
                }
            }
        }
    }

    if (configBlock == null && block == null) {
        return
    }


    install(Cohort) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}





