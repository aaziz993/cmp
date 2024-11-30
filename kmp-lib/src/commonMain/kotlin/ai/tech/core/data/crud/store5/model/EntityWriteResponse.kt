package ai.tech.core.data.crud.store5.model

public sealed class EntityWriteResponse<out T : Any> {
    public data object None : EntityWriteResponse<Nothing>()

    public data class Entities<T : Any>(val values: List<T>) : EntityWriteResponse<T>()

    public data class Update(val values: List<Boolean>) : EntityWriteResponse<Nothing>()

    public data class Count(val value: Long) : EntityWriteResponse<Nothing>()

    public data class Aggregate(val value: Any?) : EntityWriteResponse<Nothing>()
}
