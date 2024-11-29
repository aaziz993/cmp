@file:Suppress("INVISIBLE_MEMBER", "INVISIBLE_REFERENCE")

package ai.tech.core.data.database.kotysa

import ai.tech.core.data.transaction.Transaction
import kotlinx.coroutines.reactive.awaitSingle
import org.ufoss.kotysa.r2dbc.transaction.R2dbcTransactionImpl

public class KotysaTransaction(private val transaction: R2dbcTransactionImpl) : Transaction {

    override suspend fun rollback() {
        transaction.connection.rollbackTransaction().awaitSingle()
    }

    override suspend fun commit() {
        transaction.connection.commitTransaction().awaitSingle()
    }
}
