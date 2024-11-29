package ai.tech.core.data.transaction.model

import io.r2dbc.spi.IsolationLevel
import java.sql.Connection

public val TransactionIsolation.javaSqlTransactionIsolation: Int
    get() = when (this) {
        TransactionIsolation.NONE -> Connection.TRANSACTION_NONE
        TransactionIsolation.READ_UNCOMMITTED -> Connection.TRANSACTION_READ_UNCOMMITTED
        TransactionIsolation.READ_COMMITTED -> Connection.TRANSACTION_READ_COMMITTED
        TransactionIsolation.REPEATABLE_READ -> Connection.TRANSACTION_REPEATABLE_READ
        TransactionIsolation.SERIALIZABLE -> Connection.TRANSACTION_SERIALIZABLE
    }

public val TransactionIsolation.hikariTransactionIsolation: String
    get() = when (this) {
        TransactionIsolation.NONE -> "TRANSACTION_NONE"
        TransactionIsolation.READ_UNCOMMITTED -> "TRANSACTION_READ_UNCOMMITTED"
        TransactionIsolation.READ_COMMITTED -> "TRANSACTION_READ_COMMITTED"
        TransactionIsolation.REPEATABLE_READ -> "TRANSACTION_REPEATABLE_READ"
        TransactionIsolation.SERIALIZABLE -> "TRANSACTION_SERIALIZABLE"
    }

public val TransactionIsolation.r2dbcTransactionIsolation: IsolationLevel
    get() = when (this) {
        TransactionIsolation.READ_UNCOMMITTED -> IsolationLevel.READ_UNCOMMITTED
        TransactionIsolation.READ_COMMITTED -> IsolationLevel.READ_COMMITTED
        TransactionIsolation.REPEATABLE_READ -> IsolationLevel.REPEATABLE_READ
        TransactionIsolation.SERIALIZABLE -> IsolationLevel.SERIALIZABLE
        else -> throw IllegalArgumentException("Unsupported transaction isolation \"$name\"")
    }
