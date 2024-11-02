package ai.tech.core.data.database.room

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

public actual inline fun <reified T : RoomDatabase> createRoomDatabaseBuilder(databaseName: String): RoomDatabase.Builder<T> = Room.databaseBuilder(
    File(System.getProperty("java.io.tmpdir"), databaseName).absolutePath,
)

public actual inline fun <reified T : RoomDatabase> createInMemoryRoomDatabaseBuilder(): RoomDatabase.Builder<T> = Room.inMemoryDatabaseBuilder()
