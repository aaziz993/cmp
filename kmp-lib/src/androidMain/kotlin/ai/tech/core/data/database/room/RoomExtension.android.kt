package ai.tech.core.data.database.room

import androidx.room.Room
import androidx.room.RoomDatabase
import splitties.init.appCtx

public actual inline fun <reified T : RoomDatabase> createRoomDatabaseBuilder(databaseName: String): RoomDatabase.Builder<T> = Room.databaseBuilder(
    appCtx,
    T::class.java,
    databaseName,
)

public actual inline fun <reified T : RoomDatabase> createInMemoryRoomDatabaseBuilder(): RoomDatabase.Builder<T> = Room.inMemoryDatabaseBuilder(
    appCtx,
    T::class.java
)
