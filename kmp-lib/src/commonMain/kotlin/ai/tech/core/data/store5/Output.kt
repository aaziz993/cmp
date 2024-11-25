package ai.tech.core.data.store5

import ai.tech.core.data.store5.model.Model

public sealed class Output<out T : Model<*, *, *>> {
    public data class Single<T : Model<*, *, *>>(val item: T) : Output<T>()
    public data class Collection<T : Model<*, *, *>>(val items: List<T>) : Output<T>()
}
