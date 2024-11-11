package ai.tech.core.data.database.model.config

import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlin.time.DurationUnit
import kotlin.time.toDuration

@Serializable
public data class DatabaseConnectionConfig(
    val protocol: String = "r2dbc",
    val driver: String,
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
    val database: String,
    val ssl: Boolean = false,
    val connectTimeout: Duration = 15.toDuration(DurationUnit.SECONDS),
    val lockWaitTimeout: Duration = 15.toDuration(DurationUnit.SECONDS),
    val statementTimeout: Duration = 15.toDuration(DurationUnit.SECONDS),
) {

    public companion object {

        public operator fun invoke(url: String): DatabaseConnectionConfig {

            // Try matching JDBC first
            val jdbcMatchResult = jdbcUR.matchEntire(url)
            if (jdbcMatchResult != null) {
                // JDBC URL parsing
                val driver = jdbcMatchResult.groups[2]?.value ?: throw IllegalArgumentException("Driver not found")
                val host = jdbcMatchResult.groups[3]?.value ?: throw IllegalArgumentException("Host not found")
                val port = jdbcMatchResult.groups[4]?.value?.toInt() ?: throw IllegalArgumentException("Port not found")
                val database = jdbcMatchResult.groups[5]?.value
                    ?: throw IllegalArgumentException("Database name not found")

                // Default user and password for JDBC
                var user = ""
                var password = ""
                var ssl = false

                // Handle query parameters if present
                jdbcMatchResult.groups[6]?.value?.split("&")?.forEach { param ->
                    val (key, value) = param.split("=").map { it.trim() }
                    when (key) {
                        "user" -> user = value
                        "password" -> password = value
                        "ssl" -> ssl = value.toBoolean()
                    }
                }

                return DatabaseConnectionConfig(
                    driver = driver,
                    host = host,
                    port = port,
                    user = user,
                    password = password,
                    database = database,
                    ssl = ssl,
                )
            }

            // Try matching R2DBC next
            val r2dbcMatchResult = r2dbcUR.matchEntire(url)
            if (r2dbcMatchResult != null) {
                // R2DBC URL parsing
                val user = r2dbcMatchResult.groups[3]?.value ?: throw IllegalArgumentException("User not found")
                val password = r2dbcMatchResult.groups[4]?.value ?: throw IllegalArgumentException("Password not found")
                val host = r2dbcMatchResult.groups[5]?.value ?: throw IllegalArgumentException("Host not found")
                val port = r2dbcMatchResult.groups[6]?.value?.toInt()
                    ?: throw IllegalArgumentException("Port not found")
                val database = r2dbcMatchResult.groups[7]?.value
                    ?: throw IllegalArgumentException("Database name not found")
                val driver = r2dbcMatchResult.groups[2]?.value ?: throw IllegalArgumentException("Driver not found")

                // Handle query parameters for R2DBC
                val ssl = r2dbcMatchResult.groups[8]?.value?.contains("ssl=true") == true

                return DatabaseConnectionConfig(
                    driver = driver,
                    host = host,
                    port = port,
                    user = user,
                    password = password,
                    database = database,
                    ssl = ssl,
                )
            }

            throw IllegalArgumentException("Invalid database URL format")
        }
    }
}

public val jdbcUR: Regex = Regex("^jdbc:(\\w+)://([^:/]+):(\\d+)/(\\w+)(\\?.*)?$")
public val r2dbcUR: Regex = Regex("^(r2dbc:(\\w+)://)?([^:@]+):([^@]+)@([^:/]+):(\\d+)/(\\w+)(\\?.*)?$")

public val String.isJdbcUrl: Boolean
    get() = matches(jdbcUR)

public val String.isR2dbcUrl: Boolean
    get() = matches(r2dbcUR)


