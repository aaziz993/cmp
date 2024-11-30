package ai.tech.core.data.crud.store5.model

import ai.tech.core.data.crud.model.query.LimitOffset
import ai.tech.core.data.crud.model.query.Order
import ai.tech.core.data.expression.AggregateExpression
import ai.tech.core.data.expression.BooleanVariable
import ai.tech.core.data.expression.Variable

public sealed interface EntityOperation {

    public sealed interface Query : EntityOperation {

        public val dataSource: DataSource

        public data class Find(
            val projections: List<Variable>? = null,
            val sort: List<Order>? = null,
            val predicate: BooleanVariable? = null,
            val limitOffset: LimitOffset? = null,
            override val dataSource: DataSource = DataSource.all,
        ) : Query

        public data class Aggregate(
            val aggregate: AggregateExpression<*>,
            val predicate: BooleanVariable? = null,
            override val dataSource: DataSource = DataSource.all,
        ) : Query
    }

    public sealed interface Mutation : EntityOperation {

        public data object Insert : Mutation

        public data object InsertAndReturn : Mutation

        public data object Update : Mutation

        public data object Upsert : Mutation

        public data class Delete(val predicate: BooleanVariable? = null) : Mutation
    }
}
