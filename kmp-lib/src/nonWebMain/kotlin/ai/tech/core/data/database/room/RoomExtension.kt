package ai.tech.core.data.database.room

import ai.tech.core.data.database.room.keyvalue.KeyValueDatabase
import androidx.room.RoomDatabase

public expect inline fun <reified T : RoomDatabase> createRoomDatabaseBuilder(databaseName: String): RoomDatabase.Builder<T>

public expect inline fun <reified T : RoomDatabase> createInMemoryRoomDatabaseBuilder(): RoomDatabase.Builder<T>

public fun createRoomKeyValueDatabaseBuilder(databaseName: String): RoomDatabase.Builder<KeyValueDatabase> = createRoomDatabaseBuilder(databaseName)

public fun createInMemoryRoomKeyValueDatabaseBuilder(): RoomDatabase.Builder<KeyValueDatabase> = createInMemoryRoomDatabaseBuilder()
