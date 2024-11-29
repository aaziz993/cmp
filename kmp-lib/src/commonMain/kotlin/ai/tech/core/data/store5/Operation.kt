package ai.tech.core.data.store5

import ai.tech.core.data.crud.model.query.LimitOffset
import ai.tech.core.data.crud.model.query.Order
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.store5.model.DataSource
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Serializable
public sealed class Operation {

    @Serializable
    public data class Find(
        val sort: List<Order>? = null,
        val predicate: BooleanVariable? = null,
        val limitOffset: LimitOffset? = null,
        val dataSource: DataSource = DataSource.remoteOnly
    ) : Operation()

    @Serializable
    public data object Insert : Operation()

    @Serializable
    public data object Update : Operation()

    @Serializable
    public data class Delete(val predicate: BooleanVariable? = null) : Operation()

    public val id: String
        get() = Json.encodeToString(this)
}
