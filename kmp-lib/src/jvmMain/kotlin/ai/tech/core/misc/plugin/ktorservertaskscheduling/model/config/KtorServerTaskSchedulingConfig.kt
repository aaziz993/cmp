package ai.tech.core.misc.plugin.ktorservertaskscheduling.model.config

import ai.tech.core.data.cache.redis.model.config.RedisConfig
import ai.tech.core.data.database.model.config.DBConnectionConfig
import ai.tech.core.data.database.model.config.DBProviderConfig
import ai.tech.core.data.database.mongodb.model.config.MongoDBConfig
import ai.tech.core.misc.model.config.EnabledConfig
import ai.tech.core.misc.plugin.ktorservertaskscheduling.model.config.TaskConfig
import kotlinx.serialization.Serializable

@Serializable
public data class KtorServerTaskSchedulingConfig(
    val redis: Map<String?, RedisConfig> = emptyMap(),
    val jdbc: Map<String?, DBConnectionConfig> = emptyMap(),
    val mongodb: Map<String?, MongoDBConfig> = emptyMap(),
    val tasks: List<TaskConfig>? = null,
    override val enable: Boolean = true,
) : EnabledConfig
