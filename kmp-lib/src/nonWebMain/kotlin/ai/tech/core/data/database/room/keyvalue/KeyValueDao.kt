package ai.tech.core.data.database.room.keyvalue

import ai.tech.core.data.database.room.keyvalue.model.KeyValue
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
public interface KeyValueDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    public suspend fun insert(keyValue: KeyValue)

    @Query("SELECT * FROM key_values WHERE key = :key")
    public suspend fun find(key: String): KeyValue

    @Query("SELECT * FROM key_values")
    public fun getAll(): Flow<List<KeyValue>>

    @Query("DELETE FROM key_values WHERE key = :key")
    public suspend fun delete()

    @Query("DELETE FROM key_values WHERE key LIKE :key")
    public suspend fun deleteLike()

    @Query("DELETE FROM key_values")
    public suspend fun deleteAll()

    @Query("SELECT exists(SELECT 1 FROM key_values WHERE key = :key)")
    public suspend fun exists(key: String): Boolean
}
