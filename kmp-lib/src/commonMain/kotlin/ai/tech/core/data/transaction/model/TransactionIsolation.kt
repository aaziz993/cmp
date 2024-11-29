package ai.tech.core.data.transaction.model

public enum class TransactionIsolation {
    NONE,
    READ_UNCOMMITTED,
    READ_COMMITTED,
    REPEATABLE_READ,
    SERIALIZABLE
}
