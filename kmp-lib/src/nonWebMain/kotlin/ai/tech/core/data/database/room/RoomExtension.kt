package ai.tech.core.data.database.room

import ai.tech.core.data.database.room.keyvalue.model.KeyValue
import androidx.room.RoomDatabase

public expect inline fun <reified T : RoomDatabase> createRoomDatabaseBuilder(databaseName: String): RoomDatabase.Builder<T>

public expect inline fun <reified T : RoomDatabase> createInMemoryRoomDatabaseBuilder(): RoomDatabase.Builder<T>

public fun createRoomKeyValueDatabaseBuilder(databaseName: String): RoomDatabase.Builder<AppD> = createRoomDatabaseBuilder(databaseName)
