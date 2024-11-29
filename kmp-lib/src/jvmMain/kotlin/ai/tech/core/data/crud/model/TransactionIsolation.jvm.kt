package ai.tech.core.data.crud.model

public val TransactionIsolation.javaSqlTransactionIsolation: Int
    get() = when (this) {
        TransactionIsolation.NONE -> java.sql.Connection.TRANSACTION_NONE
        TransactionIsolation.READ_UNCOMMITTED -> java.sql.Connection.TRANSACTION_READ_UNCOMMITTED
        TransactionIsolation.READ_COMMITTED -> java.sql.Connection.TRANSACTION_READ_COMMITTED
        TransactionIsolation.REPEATABLE_READ -> java.sql.Connection.TRANSACTION_REPEATABLE_READ
        TransactionIsolation.SERIALIZABLE -> java.sql.Connection.TRANSACTION_SERIALIZABLE
    }

public val TransactionIsolation.hikariTransactionIsolation: String
    get() = when (this) {
        TransactionIsolation.NONE -> "TRANSACTION_NONE"
        TransactionIsolation.READ_UNCOMMITTED -> "TRANSACTION_READ_UNCOMMITTED"
        TransactionIsolation.READ_COMMITTED -> "TRANSACTION_READ_COMMITTED"
        TransactionIsolation.REPEATABLE_READ -> "TRANSACTION_REPEATABLE_READ"
        TransactionIsolation.SERIALIZABLE -> "TRANSACTION_SERIALIZABLE"
    }
