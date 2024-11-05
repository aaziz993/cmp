package ai.tech.core.data.database.keyvalue.room

import ai.tech.core.data.database.keyvalue.room.model.KeyValue
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
public interface KeyValueDao {

    @Insert
    public suspend fun insert(keyValue: KeyValue)

    @Query("SELECT * FROM key_values WHERE key = :key")
    public suspend fun select(key: String): KeyValue?

    @Query("SELECT * FROM key_values")
    public suspend fun getAll(): List<KeyValue>

    @Query("DELETE FROM key_values WHERE key = :key")
    public suspend fun deleteByKey(key: String)

    @Query("DELETE FROM key_values WHERE key LIKE :key")
    public suspend fun deleteByKeyLike(key: String)

    @Query("DELETE FROM key_values")
    public suspend fun deleteAll()

    @Query("SELECT EXISTS(SELECT 1 FROM key_values WHERE key = :key)")
    public suspend fun exists(key: String): Boolean

    @Query("SELECT COUNT(*) FROM key_values")
    public suspend fun count(): Int
}
