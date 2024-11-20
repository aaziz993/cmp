package ai.tech.core.misc.type.single

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SnapshotMutationPolicy
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.structuralEqualityPolicy
import androidx.compose.runtime.*

public fun <T> mutableStateOf(
    debounceTime: Long,
    initialValue: T,
    policy: SnapshotMutationPolicy<T> = structuralEqualityPolicy(),
): MutableState<T> {
    var lastUpdate = now
    return mutableStateOf(initialValue, policy).bimap(
        read = { it },
        write = { newValue: T ->
            now.let { now ->
                newValue.takeIf { now - lastUpdate > debounceTime }
                    ?.also { lastUpdate = now } ?: value
            }
        },
    )
}

public fun <T, R> State<T>.map(block: (T) -> R): State<R> =
    object : State<R> {
        override val value: R get() = this@map.value.let(block)
    }

public fun <T, R> MutableState<T>.bimap(
    read: (T) -> R,
    write: State<T>.(R) -> T,
): MutableState<R> =
    object : MutableState<R> {
        override var value: R
            get() = this@bimap.value.let(read)
            set(value) {
                this@bimap.value = write(value)
            }

        override fun component1(): R = value
        override fun component2(): (R) -> Unit =
            { this@bimap.value = write(it) }
    }

