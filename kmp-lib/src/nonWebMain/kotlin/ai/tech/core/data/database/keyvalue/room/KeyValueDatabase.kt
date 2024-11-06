package ai.tech.core.data.database.keyvalue.room

import ai.tech.core.data.database.keyvalue.room.model.KeyValue
import ai.tech.core.data.database.room.createInMemoryRoomDatabaseBuilder
import ai.tech.core.data.database.room.createRoomDatabaseBuilder
import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor

@Database(entities = [KeyValue::class], version = 1)
@ConstructedBy(KeyValueDatabaseConstructor::class)
public abstract class KeyValueDatabase : RoomDatabase() {

    public abstract fun getDao(): KeyValueDao
}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
public expect object KeyValueDatabaseConstructor : RoomDatabaseConstructor<KeyValueDatabase> {

    override fun initialize(): KeyValueDatabase
}

public fun createRoomKeyValueDatabaseBuilder(databaseName: String): RoomDatabase.Builder<KeyValueDatabase> = createRoomDatabaseBuilder(databaseName)

public fun createInMemoryRoomKeyValueDatabaseBuilder(): RoomDatabase.Builder<KeyValueDatabase> = createInMemoryRoomDatabaseBuilder()
