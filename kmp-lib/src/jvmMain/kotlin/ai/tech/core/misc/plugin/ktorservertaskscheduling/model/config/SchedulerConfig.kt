package ai.tech.core.misc.plugin.ktorservertaskscheduling.model.config

import dev.inmo.krontab.utils.Minutes
import kotlinx.serialization.Serializable

@Serializable
public data class SchedulerConfig(
    val offset: Int? = null,
    val milliseconds: List<Int>? = null,
    val seconds: List<Int>? = null,
    val minutes: List<Int>? = null,
    val hours: List<Int>? = null,
    val dayOfWeek: List<Int>? = null,
    val dayOfMonth: List<Int>? = null,
    val month: List<Int>? = null,
    val year: List<Int>? = null
)
