package ai.tech.core.data.database.sqldelight

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

public expect suspend fun createSQLDelightDriver(
    schema: SqlSchema<QueryResult.AsyncValue<Unit>>,
    databaseName: String
): SqlDriver

public suspend fun createSQLDelightKeyValueDatabase(databaseName: String = "KEY_VALUE"): KeyValue = KeyValue(createSQLDelightDriver(KeyValue.Schema, databaseName))
