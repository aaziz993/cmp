package ai.tech.core.data.crud.store5.model

import ai.tech.core.data.expression.BooleanVariable
import kotlinx.coroutines.flow.Flow

public sealed interface EntityOutput<out T : Any> {
    public sealed interface Typed<out T : Any> : EntityOutput<T> {

        public data class Collection<T : Any>(val values: List<T>) : Typed<T>

        public data class Stream<T : Any>(val values: Flow<T>) : Typed<T>
    }

    public sealed interface Untyped : EntityOutput<Nothing> {
        public data class Single(val value: Any?) : Untyped

        public data class Collection(val values: List<Map<String, Any?>>) : Untyped

        public data class Stream(val values: Flow<List<Any?>>) : Untyped
    }
}
