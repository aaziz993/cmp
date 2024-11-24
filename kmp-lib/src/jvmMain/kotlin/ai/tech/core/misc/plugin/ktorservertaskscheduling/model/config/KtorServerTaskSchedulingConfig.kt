package ai.tech.core.misc.plugin.ktorservertaskscheduling.model.config

import ai.tech.core.data.cache.redis.model.config.RedisConfig
import ai.tech.core.data.database.model.config.DBConnectionConfig
import ai.tech.core.data.database.mongodb.model.config.MongoDBConfig
import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class KtorServerTaskSchedulingConfig(
    val redis: Map<String?, RedisConfig> = emptyMap(),
    val jdbc: Map<String?, DBConnectionConfig> = emptyMap(),
    val mongodb: Map<String?, MongoDBConfig> = emptyMap(),
    val task: Map<String?, TaskConfig> = emptyMap(),
    override val enabled: Boolean = true,
) : EnabledConfig
