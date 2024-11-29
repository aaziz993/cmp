package ai.tech.core.data.crud

import kotlinx.datetime.TimeZone

public abstract class AbstractTypeCRUDRepository<T : Any>(
    private val createEntity: Map<String, Any?>.() -> T,
    createdAtProperty: String?,
    updatedAtProperty: String?,
    timeZone: TimeZone,
) : AbstractCRUDRepository<T>(
    createdAtProperty,
    updatedAtProperty,
    timeZone,
) {

    protected val List<T>.withCreatedAtEntities: List<T>
        get() = withCreatedAt.map(createEntity)

    protected val T.withUpdatedAtEntity: T
        get() = withUpdatedAt.createEntity()
}
