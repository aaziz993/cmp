package ai.tech.core.misc.type.multiple

import ai.tech.core.misc.type.multiple.model.AbstractAsyncIterator
import ai.tech.core.misc.type.multiple.model.AsyncIterator
import ai.tech.core.misc.type.multiple.model.ClosableAbstractAsyncIterator
import ai.tech.core.misc.type.multiple.model.ClosableAbstractIterator
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.ChannelIterator
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

public suspend fun <T> AsyncIterator<T>.next(
    count: Int,
    element: (Int, T) -> Unit,
): Int {
    var index = 0
    while (hasNext() && index < count) {
        element(index++, next())
    }
    return index
}

public suspend fun <T> AsyncIterator<T>.next(count: Int): List<T> =
    mutableListOf<T>().also { list ->
        next(count) { _, e ->
            list.add(e)
        }
    }

public fun emptyAsyncIterator(): AsyncIterator<Nothing> = EmptyAsyncIterator

public object EmptyAsyncIterator : AsyncIterator<Nothing> {
    public override suspend fun hasNext(): Boolean = false

    public override suspend fun next(): Nothing = throw IllegalStateException("Iterator is empty")
}

public inline operator fun <T> AsyncIterator<T>.iterator(): AsyncIterator<T> = this

public fun <T> AsyncIterator<T>.withIndex(): AsyncIterator<IndexedValue<T>> = IndexingAsyncIterator(this)

private class IndexingAsyncIterator<out T>(
    private val iterator: AsyncIterator<T>,
) : AsyncIterator<IndexedValue<T>> {
    private var index = 0

    override suspend fun hasNext(): Boolean = iterator.hasNext()

    override suspend fun next(): IndexedValue<T> = IndexedValue(index++, iterator.next())
}

public suspend inline fun <T> AsyncIterator<T>.forEach(operation: suspend (T) -> Unit) {
    for (element in this) operation(element)
}

public fun <T> concat(vararg iterators: AsyncIterator<T>): AsyncIterator<T> = AsyncIteratorsConcat(*iterators)

private class AsyncIteratorsConcat<T>(
    private vararg val iterators: AsyncIterator<T>,
) : AbstractAsyncIterator<T>() {
    private var index = 0

    override suspend fun computeNext() {
        do {
            if (iterators[index].hasNext()) {
                setNext(iterators[index].next())
                return
            }
        } while (++index < iterators.size)
        done()
    }
}

public fun <T> merge(iterators: List<AsyncIterator<T>>): AsyncIterator<T> = AsyncIteratorsMerge(iterators)

private class AsyncIteratorsMerge<T>(
    private val iterators: List<AsyncIterator<T>>,
) : AsyncIterator<T> {
    private var index = 0

    override suspend fun hasNext(): Boolean = iterators[0].hasNext()

    override suspend fun next(): T =
        iterators[index].next().also {
            if (++index >= iterators.size) {
                index = 0
            }
        }
}

public suspend fun <T> AsyncIterator<T>.drop(n: Int): AsyncIterator<T> {
    for (i in 0 until n) {
        if (hasNext()) {
            next()
        } else {
            throw IllegalStateException("Not enough elements to drop")
        }
    }
    return this
}

public fun <T> AsyncIterator<T>.depthIterator(
    transform: suspend AsyncIteratorDepthIterator<T>.(Int, T) -> AsyncIterator<T>?,
    removeLast: suspend () -> Unit = {},
): AsyncIterator<T> = AsyncIteratorDepthIterator(this, transform, removeLast)

public class AsyncIteratorDepthIterator<T>(
    iterator: AsyncIterator<T>,
    private val transform: suspend AsyncIteratorDepthIterator<T>.(Int, T) -> AsyncIterator<T>?,
    private val removeLast: suspend () -> Unit,
) : AbstractAsyncIterator<T>() {
    private val iterators = mutableListOf(iterator)
    private var isStop: Boolean = false

    public fun stop() {
        isStop = true
    }

    override suspend fun computeNext() {
        do {
            val last = iterators.last()
            if (last.hasNext()) {
                val next = last.next()

                val transformed = transform(iterators.size - 1, next)

                if (isStop) {
                    break
                }

                if (transformed == null) {
                    setNext(next)
                    return
                } else {
                    iterators.add(transformed)
                }
            } else {
                iterators.removeLast()
                removeLast()
            }
        } while (iterators.size > 0)

        done()
    }
}

public fun <T> AsyncIterator<T>.breadthIterator(
    transform: suspend AsyncIteratorBreadthIterator<T>.(Int, T) -> AsyncIterator<T>?,
    removeFirst: suspend () -> Unit = {},
): AsyncIterator<T> = AsyncIteratorBreadthIterator(this, transform, removeFirst)

public class AsyncIteratorBreadthIterator<T>(
    iterator: AsyncIterator<T>,
    private val transform: suspend AsyncIteratorBreadthIterator<T>.(Int, T) -> AsyncIterator<T>?,
    private val removeFirst: suspend () -> Unit = {},
) : AbstractAsyncIterator<T>() {
    private val iterators = mutableListOf(iterator)
    private var isStop: Boolean = false

    public fun stop() {
        isStop = true
    }

    override suspend fun computeNext() {
        do {
            val first = iterators.first()
            if (first.hasNext()) {
                val next = first.next()

                val transformed = transform(iterators.size - 1, next)

                if (isStop) {
                    break
                }

                if (transformed == null) {
                    setNext(next)
                    return
                } else {
                    iterators.add(transformed)
                }
            } else {
                iterators.removeFirst()
                removeFirst()
            }
        } while (iterators.size > 0)

        done()
    }
}

public fun <T> AsyncIterator<T>.filter(predicate: suspend (T) -> Boolean): AsyncIterator<T> =
    AsyncIteratorFilter(this, predicate)

private class AsyncIteratorFilter<T>(
    private val iterator: AsyncIterator<T>,
    private val predicate: suspend (T) -> Boolean,
) : AbstractAsyncIterator<T>() {
    override suspend fun computeNext() {
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (predicate(item)) {
                setNext(item)
                return
            }
        }
        done()
    }
}

public fun <T, R> AsyncIterator<T>.map(transform: (T) -> R): AsyncIterator<R> = AsyncIteratorMap(this, transform)

private class AsyncIteratorMap<T, R>(
    private val iterator: AsyncIterator<T>,
    private val transform: suspend (T) -> R,
) : AsyncIterator<R> {
    override suspend fun hasNext(): Boolean = iterator.hasNext()

    override suspend fun next(): R = transform(iterator.next())
}

public fun <T, R> AsyncIterator<T>.mapIndexed(transform: suspend (index: Int, T) -> R): AsyncIterator<R> =
    AsyncIteratorMapIndexed(this, transform)

private class AsyncIteratorMapIndexed<T, R>(
    private val iterator: AsyncIterator<T>,
    private val transform: suspend (index: Int, T) -> R,
) : AsyncIterator<R> {
    private var index = 0

    override suspend fun hasNext(): Boolean = iterator.hasNext()

    override suspend fun next(): R = transform(index++, iterator.next())
}

public fun <T, R> AsyncIterator<T>.flatMap(transform: suspend (T) -> AsyncIterator<R>): AsyncIterator<R> =
    AsyncIteratorFlatMap(this, transform)

private class AsyncIteratorFlatMap<T, R>(
    private val iterator: AsyncIterator<T>,
    private val transform: suspend (T) -> AsyncIterator<R>,
) : AbstractAsyncIterator<R>() {
    private var item: AsyncIterator<R> = EmptyAsyncIterator

    override suspend fun computeNext() {
        if (item.hasNext()) {
            setNext(item.next())
        } else {
            if (iterator.hasNext()) {
                item = transform(iterator.next())
                if (item.hasNext()) {
                    setNext(item.next())
                    return
                }
            }
            done()
        }
    }
}

public suspend fun <T> AsyncIterator<T>.startsWith(vararg elements: T): Boolean =
    elements.all {
        if (hasNext()) {
            it == next()
        } else {
            false
        }
    }

// /////////////////////////////////////////////////////ITERATOR////////////////////////////////////////////////////////
public fun <T> AsyncIterator<T>.syncIterator(coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)): Iterator<T> =
    AsyncIteratorIterator(this, coroutineScope)

private class AsyncIteratorIterator<T>(
    private val iterator: AsyncIterator<T>,
    private val coroutineScope: CoroutineScope,
) : AbstractIterator<T>() {
    override fun computeNext() {
        coroutineScope.launch {
            if (iterator.hasNext()) {
                setNext(next())
            } else {
                done()
            }
        }
    }
}

// //////////////////////////////////////////////////CHANNELITERATOR////////////////////////////////////////////////////
public fun <T> ChannelIterator<T>.asyncIterator(): AsyncIterator<T> = ChannelIteratorAsyncIterator(this)

private class ChannelIteratorAsyncIterator<T>(
    private val channelIterator: ChannelIterator<T>,
) : AsyncIterator<T> {
    override suspend fun hasNext(): Boolean = channelIterator.hasNext()

    override suspend fun next(): T = channelIterator.next()
}

public fun <T> AsyncIterator<T>.channelIterator(): ChannelIterator<T> = AsyncIteratorChannelIterator(this)

private class AsyncIteratorChannelIterator<T>(
    private val iterator: AsyncIterator<T>,
) : ChannelIterator<T> {
    private var next: T? = null
    private var nextAssigned: Boolean = false

    override suspend fun hasNext(): Boolean =
        iterator.hasNext().also {
            if (it && !nextAssigned) {
                next = iterator.next()
                nextAssigned = true
            }
        }

    override fun next(): T =
        next.also {
            nextAssigned = false
        } as T
}

// ///////////////////////////////////////////////////RECEIVECHANNEL////////////////////////////////////////////////////
@OptIn(ExperimentalCoroutinesApi::class)
public fun <T> AsyncIterator<T>.asReceiveChannel(coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Default)): ReceiveChannel<T> =
    coroutineScope.produce {
        forEach(::send)
        close()
    }

// ///////////////////////////////////////////////////BYTEREADCHANNEL///////////////////////////////////////////////////
public fun AsyncIterator<ByteArray>.asByteReadChannel(
    coroutineContext: CoroutineContext = Dispatchers.Default,
    parent: Job = Job(),
    autoFlush: Boolean = true,
): ByteReadChannel =
    CoroutineScope(coroutineContext)
        .writer(parent, autoFlush) {
            forEach(channel::writeFully)
            if (!autoFlush) {
                channel.flush()
            }
        }.channel

// ///////////////////////////////////////////////////////FLOW//////////////////////////////////////////////////////////
public fun <T> AsyncIterator<T>.asFlow(): Flow<T> = flow { forEach(::emit) }

// ////////////////////////////////////////////////////COLLECTION///////////////////////////////////////////////////////

public suspend fun <T> AsyncIterator<T>.toList(destination: MutableList<T> = ArrayList()): List<T> =
    toCollection(destination)

public suspend fun <T> AsyncIterator<T>.toSet(destination: MutableSet<T> = LinkedHashSet()): Set<T> =
    toCollection(destination)

public suspend fun <T, C : MutableCollection<in T>> AsyncIterator<T>.toCollection(destination: C): C {
    forEach(destination::add)
    return destination
}
