package ai.tech.core.data.database.room

import androidx.room.Room
import androidx.room.RoomDatabase
import splitties.init.appCtx

public actual inline fun <reified T : RoomDatabase> createRoomDatabaseBuilder(databaseName: String): RoomDatabase.Builder<T> = Room.databaseBuilder(
    appCtx,
    appCtx.getDatabasePath(databaseName).absolutePath,
)

public actual inline fun <reified T : RoomDatabase> createInMemoryRoomDatabaseBuilder(): RoomDatabase.Builder<T> = Room.inMemoryDatabaseBuilder(
    appCtx,
    T::class.java,
)
