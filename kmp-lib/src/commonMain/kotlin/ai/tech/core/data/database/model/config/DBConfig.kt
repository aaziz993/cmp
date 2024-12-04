package ai.tech.core.data.database.model.config

import ai.tech.core.data.transaction.model.TransactionIsolation
import ai.tech.core.misc.model.config.EnabledConfig
import kotlinx.serialization.Serializable
import kotlin.time.Duration
import kotlinx.serialization.Transient

@Serializable
public data class DBConfig(
    val protocol: String = "r2dbc",
    val driver: String,
    val host: String,
    val port: Int,
    val user: String,
    val password: String,
    val database: String,
    val ssl: Boolean = false,
    val initPoolSize: Int? = null,
    val maxPoolSize: Int? = null,
    val connectTimeout: Duration? = null,
    val lockWaitTimeout: Duration? = null,
    val statementTimeout: Duration? = null,
    val table: List<TableConfig> = emptyList(),
    // Only Exposed with Hikary properties
    // hikary
    val validationTimeout: Duration? = null,
    val initializationFailTimeout: Duration? = null,
    val keepaliveTime: Duration? = null,
    val isAutoCommit: Boolean? = null,
    val isReadOnly: Boolean? = null,
    val transactionIsolation: TransactionIsolation? = null,
    val useNestedTransactions: Boolean? = null,
    // exposed
    val defaultFetchSize: Int? = null,
    val defaultIsolationLevel: Int? = null,
    val defaultMaxAttempts: Int? = null,
    val defaultMinRetryDelay: Long? = null,
    val defaultMaxRetryDelay: Long? = null,
    val warnLongQueriesDuration: Long?? = null,
    val maxEntitiesToStoreInCachePerEntity: Int? = null,
    val keepLoadedReferencesOutOfTransaction: Boolean? = null,
    val defaultSchema: SchemaConfig? = null,
    val logTooMuchResultSetsThreshold: Int? = null,
    val preserveKeywordCasing: Boolean? = null,
    override val enabled: Boolean = true
) : EnabledConfig {

    @Transient
    val jdbcUrl: String = "jdbc:$driver://$host:$port/$database${
        if (ssl) {
            "?${
                when (driver) {
                    "mysql" -> "sslMode=VERIFY_IDENTITY"
                    else -> "ssl=true"
                }
            }"
        }
        else {
            ""
        }
    }"

    @Transient
    val r2dbcUrl: String = "r2dbc:$driver://${user}:$password@$host:$port/database${
        listOfNotNull(
            if (ssl) {
                when (driver) {
                    "mysql" -> "sslMode=VERIFY_IDENTITY"
                    else -> "ssl=true"
                }
            }
            else {
                null
            },
            connectTimeout?.let { "connectTimeout=${it.inWholeMilliseconds}" },
            lockWaitTimeout?.let { "lockWaitTimeout=${it.inWholeMilliseconds}" },
            statementTimeout?.let { "statementTimeout=${it.inWholeMilliseconds}" },
        ).let {
            if (it.isEmpty()) {
                ""
            }
            else {
                "?${it.joinToString("&")}"
            }
        }
    }"

    public companion object {

        public operator fun invoke(
            url: String,
            username: String = "",
            password: String = "",
            initPoolSize: Int? = null,
            maxPoolSize: Int? = null,
            connectTimeout: Duration? = null,
            lockWaitTimeout: Duration? = null,
            statementTimeout: Duration? = null,
            table: List<TableConfig> = emptyList(),
            // Only Exposed with Hikary properties
            // hikary
            validationTimeout: Duration? = null,
            initializationFailTimeout: Duration? = null,
            keepaliveTime: Duration? = null,
            isAutoCommit: Boolean? = null,
            isReadOnly: Boolean? = null,
            transactionIsolation: TransactionIsolation? = null,
            useNestedTransactions: Boolean? = null,
            // exposed
            defaultFetchSize: Int? = null,
            defaultIsolationLevel: Int? = null,
            defaultMaxAttempts: Int? = null,
            defaultMinRetryDelay: Long? = null,
            defaultMaxRetryDelay: Long? = null,
            warnLongQueriesDuration: Long?? = null,
            maxEntitiesToStoreInCachePerEntity: Int? = null,
            keepLoadedReferencesOutOfTransaction: Boolean? = null,
            defaultSchema: SchemaConfig? = null,
            logTooMuchResultSetsThreshold: Int? = null,
            preserveKeywordCasing: Boolean? = null,
        ): DBConfig {

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
                var username = username
                var password = password
                var ssl = false

                // Handle query parameters if present
                jdbcMatchResult.groups[6]?.value?.split("&")?.forEach { param ->
                    val (key, value) = param.split("=").map { it.trim() }
                    when (key) {
                        "user" -> username = value
                        "password" -> password = value
                        "ssl" -> ssl = value.toBoolean()
                    }
                }

                return DBConfig(
                    "jdbc",
                    driver,
                    host,
                    port,
                    username,
                    password,
                    database,
                    ssl,
                    initPoolSize,
                    maxPoolSize,
                    connectTimeout,
                    lockWaitTimeout,
                    statementTimeout,
                    table,
                    // Only Exposed with Hikary properties
                    // hikary
                    validationTimeout,
                    initializationFailTimeout,
                    keepaliveTime,
                    isAutoCommit,
                    isReadOnly,
                    transactionIsolation,
                    useNestedTransactions,
                    // exposed
                    defaultFetchSize,
                    defaultIsolationLevel,
                    defaultMaxAttempts,
                    defaultMinRetryDelay,
                    defaultMaxRetryDelay,
                    warnLongQueriesDuration,
                    maxEntitiesToStoreInCachePerEntity,
                    keepLoadedReferencesOutOfTransaction,
                    defaultSchema,
                    logTooMuchResultSetsThreshold,
                    preserveKeywordCasing,
                )
            }

            // Try matching R2DBC next
            val r2dbcMatchResult = r2dbcUR.matchEntire(url)
            if (r2dbcMatchResult != null) {
                // R2DBC URL parsing
                val username = r2dbcMatchResult.groups[3]?.value ?: username
                val password = r2dbcMatchResult.groups[4]?.value ?: password
                val host = r2dbcMatchResult.groups[5]?.value ?: throw IllegalArgumentException("Host not found")
                val port = r2dbcMatchResult.groups[6]?.value?.toInt()
                    ?: throw IllegalArgumentException("Port not found")
                val database = r2dbcMatchResult.groups[7]?.value
                    ?: throw IllegalArgumentException("Database name not found")
                val driver = r2dbcMatchResult.groups[2]?.value ?: throw IllegalArgumentException("Driver not found")

                // Handle query parameters for R2DBC
                val ssl = r2dbcMatchResult.groups[8]?.value?.contains("ssl=true") == true

                return DBConfig(
                    "r2dbc",
                    driver,
                    host,
                    port,
                    username,
                    password,
                    database,
                    ssl,
                    initPoolSize,
                    maxPoolSize,
                    connectTimeout,
                    lockWaitTimeout,
                    statementTimeout,
                    table,
                    // Only Exposed with Hikary properties
                    // hikary
                    validationTimeout,
                    initializationFailTimeout,
                    keepaliveTime,
                    isAutoCommit,
                    isReadOnly,
                    transactionIsolation,
                    useNestedTransactions,
                    // exposed
                    defaultFetchSize,
                    defaultIsolationLevel,
                    defaultMaxAttempts,
                    defaultMinRetryDelay,
                    defaultMaxRetryDelay,
                    warnLongQueriesDuration,
                    maxEntitiesToStoreInCachePerEntity,
                    keepLoadedReferencesOutOfTransaction,
                    defaultSchema,
                    logTooMuchResultSetsThreshold,
                    preserveKeywordCasing,
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

public fun db(
    url: String,
    username: String = "",
    password: String = "",
    initPoolSize: Int? = null,
    maxPoolSize: Int? = null,
    connectTimeout: Duration? = null,
    lockWaitTimeout: Duration? = null,
    statementTimeout: Duration? = null,
    table: List<TableConfig> = emptyList(),
    // Only Exposed with Hikary properties
    // hikary
    validationTimeout: Duration? = null,
    initializationFailTimeout: Duration? = null,
    keepaliveTime: Duration? = null,
    isAutoCommit: Boolean? = null,
    isReadOnly: Boolean? = null,
    transactionIsolation: TransactionIsolation? = null,
    useNestedTransactions: Boolean? = null,
    // exposed
    defaultFetchSize: Int? = null,
    defaultIsolationLevel: Int? = null,
    defaultMaxAttempts: Int? = null,
    defaultMinRetryDelay: Long? = null,
    defaultMaxRetryDelay: Long? = null,
    warnLongQueriesDuration: Long?? = null,
    maxEntitiesToStoreInCachePerEntity: Int? = null,
    keepLoadedReferencesOutOfTransaction: Boolean? = null,
    defaultSchema: SchemaConfig? = null,
    logTooMuchResultSetsThreshold: Int? = null,
    preserveKeywordCasing: Boolean? = null,
): DBConfig = DBConfig(
    url,
    username,
    password,
    initPoolSize,
    maxPoolSize,
    connectTimeout,
    lockWaitTimeout,
    statementTimeout,
    table,
    // Only Exposed with Hikary properties
    // hikary
    validationTimeout,
    initializationFailTimeout,
    keepaliveTime,
    isAutoCommit,
    isReadOnly,
    transactionIsolation,
    useNestedTransactions,
    // exposed
    defaultFetchSize,
    defaultIsolationLevel,
    defaultMaxAttempts,
    defaultMinRetryDelay,
    defaultMaxRetryDelay,
    warnLongQueriesDuration,
    maxEntitiesToStoreInCachePerEntity,
    keepLoadedReferencesOutOfTransaction,
    defaultSchema,
    logTooMuchResultSetsThreshold,
    preserveKeywordCasing,
)
