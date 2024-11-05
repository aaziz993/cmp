package ai.tech.core.data.database.keyvalue.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey

@Fts4
@Entity(tableName = "key_values", indices = [Index(value = ["key"], unique = true)])
public class KeyValue(
    @PrimaryKey(autoGenerate = true)
    public val id: Long = 0,
    public val key: String,
    public val value: String?,
)
