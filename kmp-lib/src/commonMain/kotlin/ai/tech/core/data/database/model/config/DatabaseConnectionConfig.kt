package ai.tech.core.data.database.model.config

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Serializable
public data class DatabaseConnectionConfig(
    val driver: String,
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
    val database: String,
    val protocol: String? = null,
    val ssl: Boolean = false,
    val connectTimeout: Duration = 15.toDuration(DurationUnit.SECONDS),
    val lockWaitTimeout: Duration = 15.toDuration(DurationUnit.SECONDS),
    val statementTimeout: Duration = 15.toDuration(DurationUnit.SECONDS),
)
