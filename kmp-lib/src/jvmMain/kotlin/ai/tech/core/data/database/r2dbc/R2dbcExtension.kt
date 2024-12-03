package ai.tech.core.data.database.r2dbc

import io.r2dbc.spi.ConnectionMetadata
import io.r2dbc.spi.Option

public val AUTO_COMMIT: Option<Boolean> = Option.valueOf("autoCommit")

public val USE_NESTED_TRANSACTIONS: Option<Boolean> = Option.valueOf("useNestedTransactions")

public val ConnectionMetadata.supportsSavepoints: Boolean
    get() = let {
        val productName = it.databaseProductName.lowercase()
        val productVersion = it.databaseVersion

        when {
            productName.contains("postgresql") -> true
            productName.contains("sql server") -> true
            productName.contains("oracle") -> true
            productName.contains("mariadb") -> productVersion >= "10.3"
            productName.contains("mysql") -> productVersion >= "5.0"
            productName.contains("db2") -> true
            productName.contains("h2") -> true
            productName.contains("mssql") || productName.contains("sql server") -> true
            else -> false
        }
    }
