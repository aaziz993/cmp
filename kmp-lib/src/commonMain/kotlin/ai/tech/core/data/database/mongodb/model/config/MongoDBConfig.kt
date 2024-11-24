package ai.tech.core.data.database.mongodb.model.config

import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable

@Serializable
public data class MongoDBConfig(
    val connectionString: String,
    val databaseName: String,
    override val enabled: Boolean = true
) : EnabledConfig
