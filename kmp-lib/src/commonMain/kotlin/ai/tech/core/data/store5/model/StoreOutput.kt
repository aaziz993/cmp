package ai.tech.core.data.store5.model

import kotlinx.coroutines.flow.Flow

public sealed interface StoreOutput<out T : Any> {
    public sealed interface Typed<out T : Any> : StoreOutput<T> {
        public data class Single<T>(val value: T) : Typed<Nothing>

        public data class Collection<T : Any>(val values: List<T>) : Typed<T>

        public data class Stream<T : Any>(val values: Flow<T>) : Typed<T>
    }

    public sealed interface Untyped : StoreOutput<Nothing> {
        public data class Collection(val values: List<Map<String, Any?>>) : Untyped

        public data class Stream(val values: Flow<List<Any?>>) : Untyped
    }
}
