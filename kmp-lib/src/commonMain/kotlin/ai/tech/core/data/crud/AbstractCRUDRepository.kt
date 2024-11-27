package ai.tech.core.data.crud

import kotlinx.datetime.TimeZone

public abstract class AbstractCRUDRepository<T : Any, ID : Any>(
    public val createdAtProperty: String?,
    public val updatedAtProperty: String?,
    timeZone: TimeZone,
) : CRUDRepository<T, ID> {
    protected abstract val createdAtNow: ((TimeZone) -> Any)?

    protected abstract val updatedAtNow: ((TimeZone) -> Any)?

    protected val entityCreatedAtAware: (T) -> Map<String, Any?> =
        { entity -> entity.propertyValues + (createdAtProperty!! to createdAtNow!!(timeZone)) }

    protected val entityUpdatedAtAware: (T) -> Map<String, Any?> =
        { entity -> entity.propertyValues + (updatedAtProperty!! to updatedAtNow!!(timeZone)) }

    protected abstract val T.propertyValues: Map<String, Any?>
}
