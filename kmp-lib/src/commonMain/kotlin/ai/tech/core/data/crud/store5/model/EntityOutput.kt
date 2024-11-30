package ai.tech.core.data.crud.store5.model

import ai.tech.core.data.expression.BooleanVariable
import kotlinx.coroutines.flow.Flow

public sealed interface EntityOutput<out Domain : Any> {
    public sealed interface Typed<out Domain : Any> : EntityOutput<Domain> {
        public data class Predicate(val value: BooleanVariable?) : EntityOutput<Nothing>

        public data class Collection<Domain : Any>(val values: List<Domain>) : Typed<Domain>

        public data class Stream<Domain : Any>(val values: Flow<Domain>) : Typed<Domain>
    }

    public sealed interface Untyped : EntityOutput<Nothing> {
        public data class Single(val value: Any?) : Untyped

        public data class Collection(val values: List<Map<String, Any?>>) : Untyped

        public data class Stream(val values: Flow<List<Any?>>) : Untyped
    }
}
