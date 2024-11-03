package ai.tech.core.data.database.room.keyvalue.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.Index
import androidx.room.PrimaryKey

@Fts4
@Entity(tableName = "key_values", indices = [Index(value = ["key"], unique = true)])
public class KeyValue(
    @PrimaryKey
    @ColumnInfo(name = "id")
    public val id: Long,

    @ColumnInfo(name = "key")
    public val key: String,

    @ColumnInfo(name = "value")
    public val value: String,
)
