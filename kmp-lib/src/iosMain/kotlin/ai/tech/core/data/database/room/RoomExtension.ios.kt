package ai.tech.core.data.database.room

import ai.tech.core.misc.type.create
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.serialization.json.Json
import platform.Foundation.NSHomeDirectory

public actual inline fun <reified T : RoomDatabase> createRoomDatabaseBuilder(databaseName: String): RoomDatabase.Builder<T> = Room.databaseBuilder(
        NSHomeDirectory() + "/$databaseName",
)

public actual inline fun <reified T : RoomDatabase> createInMemoryRoomDatabaseBuilder(): RoomDatabase.Builder<T> = Room.inMemoryDatabaseBuilder()


