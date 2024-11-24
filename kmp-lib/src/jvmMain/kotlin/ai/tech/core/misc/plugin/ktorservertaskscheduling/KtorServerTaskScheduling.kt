package ai.tech.core.misc.plugin.ktorservertaskscheduling

import ai.tech.core.data.database.model.config.hikariDataSource
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.ktorservertaskscheduling.model.config.KtorServerTaskSchedulingConfig
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.flaxoos.ktor.server.plugins.taskscheduling.TaskScheduling
import io.github.flaxoos.ktor.server.plugins.taskscheduling.TaskSchedulingConfiguration
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.lock.database.jdbc
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.lock.database.mongoDb
import io.github.flaxoos.ktor.server.plugins.taskscheduling.managers.lock.redis.redis
import io.ktor.server.application.*
import korlibs.time.DateTime
import org.jetbrains.exposed.sql.Database

public fun Application.configureKtorServerTaskScheduling(
    config: KtorServerTaskSchedulingConfig?,
    tasks: Map<String?, Map<String?, (executionTime: DateTime) -> Unit>>,
    block: (TaskSchedulingConfiguration.() -> Unit)? = null) {
    val configBlock: (TaskSchedulingConfiguration.() -> Unit)? = config?.takeIf(EnabledConfig::enabled)?.let {
        {
            it.redis.filterValues(EnabledConfig::enabled).forEach { name, config ->
                redis(name) {
                    config.host?.let { host = it }
                    config.port?.let { port = it }
                    config.username?.let { username = it }
                    config.password?.let { password = it }
                    config.lockExpirationMs?.let { lockExpirationMs = it }
                    config.connectionPoolInitialSize?.let { connectionPoolInitialSize = it }
                    config.connectionPoolMaxSize?.let { connectionPoolMaxSize = it }
                    config.connectionAcquisitionTimeoutMs?.let { connectionAcquisitionTimeoutMs = it }
                }
            }

            it.jdbc.filterValues(EnabledConfig::enabled).forEach { name, config ->
                jdbc(name) {
                    database = Database.connect(config.hikariDataSource)
                }
            }

            it.mongodb.filterValues(EnabledConfig::enabled).forEach { name, config ->
                mongoDb(name) {
                    client = MongoClient.create(config.connectionString)
                    databaseName = config.databaseName
                }
            }

            it.task.filterValues(EnabledConfig::enabled).forEach { (name, config) ->
                task(config.taskManagerName) { // if no taskManagerName is provided, the task would be assigned to the default manager
                    name?.let { this.name = it }

                    task = { executionTime ->
                        log.info("Task \"$name\"  ${config.taskManagerName?.let { "with task manager \"$it\"" }} is running: $executionTime")

                        tasks[config.taskManagerName]?.get(name)?.invoke(executionTime)
                    }

                    kronSchedule = {
                        config.scheduler.milliseconds?.let {
                            milliseconds {
                                include(it.toTypedArray())
                            }
                        }

                        config.scheduler.seconds?.let {
                            seconds {
                                include(it.toTypedArray())
                            }
                        }

                        config.scheduler.minutes?.let {
                            minutes {
                                include(it.toTypedArray())
                            }
                        }

                        config.scheduler.hours?.let {
                            hours {
                                include(it.toTypedArray())
                            }
                        }

                        config.scheduler.dayOfWeek?.let {
                            dayOfWeek {
                                include(it.toTypedArray())
                            }
                        }

                        config.scheduler.dayOfMonth?.let {
                            dayOfMonth {
                                include(it.toTypedArray())
                            }
                        }

                        config.scheduler.month?.let {
                            months {
                                include(it.toTypedArray())
                            }
                        }

                        config.scheduler.year?.let {
                            years {
                                include(it.toTypedArray())
                            }
                        }
                    }

                    config.concurrency?.let { concurrency = it }
                }
            }
        }
    }

    if (configBlock == null && block == null) {
        return
    }

    install(TaskScheduling) {
        configBlock?.invoke(this)

        block?.invoke(this)
    }
}
