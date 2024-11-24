package ai.tech.core.misc.plugin.ktorservertaskscheduling.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class TaskConfig(
    val taskManagerName: String? = null,
    val scheduler: SchedulerConfig,
    val concurrency: Int? = null,
    override val enabled: Boolean = true,
) : EnabledConfig
