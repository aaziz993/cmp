package ai.tech.core.data.crud

import kotlinx.datetime.TimeZone

public abstract class AbstractCRUDRepository<T : Any>(
    protected val getEntityPropertyValues: (T) -> Map<String, Any?>,
    public val createdAtProperty: String?,
    public val updatedAtProperty: String?,
    timeZone: TimeZone,
) : CRUDRepository<T> {

    protected abstract val createdAtNow: ((TimeZone) -> Any)?

    protected abstract val updatedAtNow: ((TimeZone) -> Any)?

    protected val entityCreatedAtAware: (T) -> Map<String, Any?> =
        { entity -> getEntityPropertyValues(entity) + (createdAtProperty!! to createdAtNow!!(timeZone)) }

    protected val entityUpdatedAtAware: (T) -> Map<String, Any?> =
        { entity -> getEntityPropertyValues(entity) + (updatedAtProperty!! to updatedAtNow!!(timeZone)) }
}
