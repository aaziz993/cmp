package ai.tech.core.data.crud

import kotlinx.datetime.TimeZone

public abstract class AbstractCRUDRepository<T : Any>(
    public val createdAtProperty: String?,
    public val updatedAtProperty: String?,
    public val timeZone: TimeZone,
) : CRUDRepository<T> {

    protected abstract val T.propertyValues: Map<String, Any?>

    protected abstract val createdAtNow: ((TimeZone) -> Any)?

    protected abstract val updatedAtNow: ((TimeZone) -> Any)?

    protected val List<T>.withCreatedAt: List<Map<String, Any?>>
        get() = map { entity -> entity.propertyValues.withCreatedAt() }

    protected val T.withUpdatedAt: Map<String, Any?>
        get() = propertyValues.withUpdatedAt()

    protected val withCreatedAt: Map<String, Any?>.() -> Map<String, Any?> = if (createdAtNow == null) {
        { this }
    }
    else {
        { this + (createdAtProperty!! to createdAtNow!!(timeZone)) }
    }

    protected val withUpdatedAt: Map<String, Any?>.() -> Map<String, Any?> = if (updatedAtNow == null) {
        { this }
    }
    else {
        { this + (updatedAtProperty!! to updatedAtNow!!(timeZone)) }
    }
}
