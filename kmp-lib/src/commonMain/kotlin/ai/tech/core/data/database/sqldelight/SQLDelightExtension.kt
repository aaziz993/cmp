package ai.tech.core.data.database.sqldelight

import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema

public expect suspend fun createSQLDelightDriver(
    schema: SqlSchema<QueryResult.AsyncValue<Unit>>,
    databaseName: String
): SqlDriver

public expect suspend fun createInMemorySQLDelightDriver(
    schema: SqlSchema<QueryResult.AsyncValue<Unit>>,
    databaseName: String
): SqlDriver

public suspend fun createSQLDelightKeyValueDatabase(databaseName: String = "key_value"): KeyValue = KeyValue(createSQLDelightDriver(KeyValue.Schema, databaseName))

public suspend fun createInMemorySQLDelightKeyValueDatabase(databaseName: String = "in_memory_key_value"): KeyValue = KeyValue(createInMemorySQLDelightDriver(KeyValue.Schema, databaseName))
