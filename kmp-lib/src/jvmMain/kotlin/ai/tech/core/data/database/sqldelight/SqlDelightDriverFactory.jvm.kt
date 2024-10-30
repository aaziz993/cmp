package ai.tech.core.data.database.sqldelight

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlSchema
import java.io.File

@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
public actual class SqlDelightDriverFactory {

    public actual suspend fun createDriver(
        schema: SqlSchema<QueryResult.AsyncValue<Unit>>,
        databaseName: String
    ): SqlDriver =
        JdbcSqliteDriver(url = "jdbc:sqlite:${File(System.getProperty("java.io.tmpdir"), "$databaseName.db").path}")
            .also { schema.create(it).await() }
}