package ai.tech.core.data.database.room

import ai.tech.core.misc.type.create
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.serialization.json.Json
import platform.Foundation.NSHomeDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSUserDomainMask

public actual inline fun <reified T : RoomDatabase> createRoomDatabaseBuilder(databaseName: String): RoomDatabase.Builder<T> = Room.databaseBuilder(
    requireNotNull(NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )?.path) + "/$databaseName",
)

public actual inline fun <reified T : RoomDatabase> createInMemoryRoomDatabaseBuilder(): RoomDatabase.Builder<T> = Room.inMemoryDatabaseBuilder()
